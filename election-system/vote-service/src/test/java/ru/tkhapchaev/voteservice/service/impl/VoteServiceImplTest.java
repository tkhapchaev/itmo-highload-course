package ru.tkhapchaev.voteservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.tkhapchaev.voteservice.client.ElectionServiceClient;
import ru.tkhapchaev.voteservice.client.VoterServiceClient;
import ru.tkhapchaev.voteservice.dto.internal.CandidateLookupResponse;
import ru.tkhapchaev.voteservice.dto.internal.ElectionStatusLookupResponse;
import ru.tkhapchaev.voteservice.dto.internal.VoterLookupResponse;
import ru.tkhapchaev.voteservice.entity.VoteEntity;
import ru.tkhapchaev.voteservice.kafka.VoteCreatedEvent;
import ru.tkhapchaev.voteservice.kafka.VoteEventPublisher;
import ru.tkhapchaev.voteservice.repository.VoteRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteServiceImplTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private ElectionServiceClient electionServiceClient;

    @Mock
    private VoterServiceClient voterServiceClient;

    @Mock
    private VoteEventPublisher voteEventPublisher;

    @InjectMocks
    private VoteServiceImpl voteService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(voteService, "voteCreatedTopic", "vote-created");
    }

    @Test
    void create_shouldSaveVoteAndPublishEvent() {
        UUID candidateId = UUID.randomUUID();
        UUID voterId = UUID.randomUUID();
        UUID electionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID voteId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

        VoteEntity input = new VoteEntity();
        input.setCandidateId(candidateId);
        input.setVoterId(voterId);

        VoteEntity saved = new VoteEntity();
        saved.setId(voteId);
        saved.setCandidateId(candidateId);
        saved.setVoterId(voterId);
        saved.setElectionId(electionId);
        saved.setUserId(userId);
        saved.setCreatedAt(createdAt);

        when(voteRepository.existsByVoterId(voterId)).thenReturn(false);
        when(electionServiceClient.getCandidate(candidateId))
                .thenReturn(new CandidateLookupResponse(candidateId, electionId));
        when(voterServiceClient.getVoter(voterId))
                .thenReturn(new VoterLookupResponse(voterId, userId, electionId));
        when(electionServiceClient.getElectionStatus(electionId))
                .thenReturn(new ElectionStatusLookupResponse(1, "ACTIVE"));
        when(voteRepository.existsByUserIdAndElectionId(userId, electionId)).thenReturn(false);
        when(voteRepository.save(input)).thenReturn(saved);

        VoteEntity result = voteService.create(input);

        assertThat(result).isEqualTo(saved);
        assertThat(input.getElectionId()).isEqualTo(electionId);
        assertThat(input.getUserId()).isEqualTo(userId);

        ArgumentCaptor<VoteCreatedEvent> eventCaptor = ArgumentCaptor.forClass(VoteCreatedEvent.class);
        verify(voteEventPublisher).publishVoteCreated(eventCaptor.capture(), eq("vote-created"));

        VoteCreatedEvent event = eventCaptor.getValue();
        assertThat(event.voteId()).isEqualTo(voteId);
        assertThat(event.candidateId()).isEqualTo(candidateId);
        assertThat(event.voterId()).isEqualTo(voterId);
        assertThat(event.electionId()).isEqualTo(electionId);
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.createdAt()).isEqualTo(createdAt);
    }

    @Test
    void create_shouldThrow_whenVoterAlreadyVoted() {
        UUID voterId = UUID.randomUUID();

        VoteEntity input = new VoteEntity();
        input.setCandidateId(UUID.randomUUID());
        input.setVoterId(voterId);

        when(voteRepository.existsByVoterId(voterId)).thenReturn(true);

        assertThatThrownBy(() -> voteService.create(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already voted");

        verify(voteRepository, never()).save(any());
        verifyNoInteractions(electionServiceClient, voterServiceClient, voteEventPublisher);
    }

    @Test
    void create_shouldThrow_whenCandidateAndVoterFromDifferentElections() {
        UUID candidateId = UUID.randomUUID();
        UUID voterId = UUID.randomUUID();
        UUID electionA = UUID.randomUUID();
        UUID electionB = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        VoteEntity input = new VoteEntity();
        input.setCandidateId(candidateId);
        input.setVoterId(voterId);

        when(voteRepository.existsByVoterId(voterId)).thenReturn(false);
        when(electionServiceClient.getCandidate(candidateId))
                .thenReturn(new CandidateLookupResponse(candidateId, electionA));
        when(voterServiceClient.getVoter(voterId))
                .thenReturn(new VoterLookupResponse(voterId, userId, electionB));

        assertThatThrownBy(() -> voteService.create(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("different elections");

        verify(voteRepository, never()).save(any());
        verify(voteEventPublisher, never()).publishVoteCreated(any(), any());
    }

    @Test
    void create_shouldThrow_whenElectionIsNotActive() {
        UUID candidateId = UUID.randomUUID();
        UUID voterId = UUID.randomUUID();
        UUID electionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        VoteEntity input = new VoteEntity();
        input.setCandidateId(candidateId);
        input.setVoterId(voterId);

        when(voteRepository.existsByVoterId(voterId)).thenReturn(false);
        when(electionServiceClient.getCandidate(candidateId))
                .thenReturn(new CandidateLookupResponse(candidateId, electionId));
        when(voterServiceClient.getVoter(voterId))
                .thenReturn(new VoterLookupResponse(voterId, userId, electionId));
        when(electionServiceClient.getElectionStatus(electionId))
                .thenReturn(new ElectionStatusLookupResponse(0, "OPEN"));

        assertThatThrownBy(() -> voteService.create(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ACTIVE elections");

        verify(voteRepository, never()).save(any());
        verify(voteEventPublisher, never()).publishVoteCreated(any(), any());
    }

    @Test
    void create_shouldThrow_whenUserAlreadyVotedInElection() {
        UUID candidateId = UUID.randomUUID();
        UUID voterId = UUID.randomUUID();
        UUID electionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        VoteEntity input = new VoteEntity();
        input.setCandidateId(candidateId);
        input.setVoterId(voterId);

        when(voteRepository.existsByVoterId(voterId)).thenReturn(false);
        when(electionServiceClient.getCandidate(candidateId))
                .thenReturn(new CandidateLookupResponse(candidateId, electionId));
        when(voterServiceClient.getVoter(voterId))
                .thenReturn(new VoterLookupResponse(voterId, userId, electionId));
        when(electionServiceClient.getElectionStatus(electionId))
                .thenReturn(new ElectionStatusLookupResponse(1, "ACTIVE"));
        when(voteRepository.existsByUserIdAndElectionId(userId, electionId)).thenReturn(true);

        assertThatThrownBy(() -> voteService.create(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already voted in this election");

        verify(voteRepository, never()).save(any());
        verify(voteEventPublisher, never()).publishVoteCreated(any(), any());
    }

    @Test
    void update_shouldSaveVote_whenDataIsValid() {
        UUID voteId = UUID.randomUUID();
        UUID existingVoterId = UUID.randomUUID();
        UUID newVoterId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();
        UUID electionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        VoteEntity existing = new VoteEntity();
        existing.setId(voteId);
        existing.setVoterId(existingVoterId);

        VoteEntity update = new VoteEntity();
        update.setCandidateId(candidateId);
        update.setVoterId(newVoterId);

        when(voteRepository.findById(voteId)).thenReturn(Optional.of(existing));
        when(voteRepository.existsByVoterId(newVoterId)).thenReturn(false);
        when(electionServiceClient.getCandidate(candidateId))
                .thenReturn(new CandidateLookupResponse(candidateId, electionId));
        when(voterServiceClient.getVoter(newVoterId))
                .thenReturn(new VoterLookupResponse(newVoterId, userId, electionId));
        when(electionServiceClient.getElectionStatus(electionId))
                .thenReturn(new ElectionStatusLookupResponse(1, "ACTIVE"));
        when(voteRepository.save(existing)).thenReturn(existing);

        VoteEntity result = voteService.update(voteId, update);

        assertThat(result).isEqualTo(existing);
        assertThat(existing.getCandidateId()).isEqualTo(candidateId);
        assertThat(existing.getVoterId()).isEqualTo(newVoterId);
        assertThat(existing.getElectionId()).isEqualTo(electionId);
        assertThat(existing.getUserId()).isEqualTo(userId);
        verify(voteEventPublisher, never()).publishVoteCreated(any(), any());
    }

    @Test
    void update_shouldThrow_whenNewVoterAlreadyVoted() {
        UUID voteId = UUID.randomUUID();
        UUID existingVoterId = UUID.randomUUID();
        UUID newVoterId = UUID.randomUUID();

        VoteEntity existing = new VoteEntity();
        existing.setId(voteId);
        existing.setVoterId(existingVoterId);

        VoteEntity update = new VoteEntity();
        update.setVoterId(newVoterId);
        update.setCandidateId(UUID.randomUUID());

        when(voteRepository.findById(voteId)).thenReturn(Optional.of(existing));
        when(voteRepository.existsByVoterId(newVoterId)).thenReturn(true);

        assertThatThrownBy(() -> voteService.update(voteId, update))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already voted");

        verify(voteRepository, never()).save(any());
    }

    @Test
    void getById_shouldReturnVote() {
        UUID id = UUID.randomUUID();
        VoteEntity vote = new VoteEntity();
        vote.setId(id);

        when(voteRepository.findById(id)).thenReturn(Optional.of(vote));

        VoteEntity result = voteService.getById(id);

        assertThat(result).isEqualTo(vote);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(voteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> voteService.getById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void getAll_shouldReturnByCandidate_whenCandidateProvided() {
        UUID candidateId = UUID.randomUUID();
        List<VoteEntity> votes = List.of(new VoteEntity(), new VoteEntity());
        when(voteRepository.findByCandidateId(candidateId)).thenReturn(votes);

        List<VoteEntity> result = voteService.getAll(candidateId, UUID.randomUUID());

        assertThat(result).isEqualTo(votes);
        verify(voteRepository, never()).findByUserId(any());
        verify(voteRepository, never()).findAll();
    }

    @Test
    void getAll_shouldReturnByUser_whenOnlyUserProvided() {
        UUID userId = UUID.randomUUID();
        List<VoteEntity> votes = List.of(new VoteEntity());
        when(voteRepository.findByUserId(userId)).thenReturn(votes);

        List<VoteEntity> result = voteService.getAll(null, userId);

        assertThat(result).isEqualTo(votes);
        verify(voteRepository, never()).findAll();
    }

    @Test
    void getAll_shouldReturnAll_whenNoFiltersProvided() {
        List<VoteEntity> votes = List.of(new VoteEntity(), new VoteEntity());
        when(voteRepository.findAll()).thenReturn(votes);

        List<VoteEntity> result = voteService.getAll(null, null);

        assertThat(result).isEqualTo(votes);
    }

    @Test
    void countByElectionId_shouldReturnCount() {
        UUID electionId = UUID.randomUUID();
        when(voteRepository.countByElectionId(electionId)).thenReturn(7L);

        long result = voteService.countByElectionId(electionId);

        assertThat(result).isEqualTo(7L);
    }

    @Test
    void delete_shouldDeleteExistingVote() {
        UUID id = UUID.randomUUID();
        VoteEntity vote = new VoteEntity();
        vote.setId(id);

        when(voteRepository.findById(id)).thenReturn(Optional.of(vote));

        voteService.delete(id);

        verify(voteRepository).delete(vote);
    }
}
