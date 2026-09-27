package ru.tkhapchaev.electionservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tkhapchaev.electionservice.entity.Candidate;
import ru.tkhapchaev.electionservice.entity.Election;
import ru.tkhapchaev.electionservice.entity.ElectionStatus;
import ru.tkhapchaev.electionservice.entity.ElectionStatusCode;
import ru.tkhapchaev.electionservice.repository.CandidateRepository;
import ru.tkhapchaev.electionservice.repository.ElectionRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private ElectionRepository electionRepository;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    @Test
    void create_shouldSaveCandidate_whenElectionIsOpen() {
        UUID electionId = UUID.randomUUID();

        Election election = buildElection(electionId, ElectionStatusCode.OPEN);
        Candidate candidate = new Candidate();
        Election candidateElection = new Election();
        candidateElection.setId(electionId);
        candidate.setElection(candidateElection);

        when(electionRepository.findById(electionId)).thenReturn(Optional.of(election));
        when(candidateRepository.save(candidate)).thenReturn(candidate);

        Candidate result = candidateService.create(candidate);

        assertThat(result).isEqualTo(candidate);
        verify(candidateRepository).save(candidate);
    }

    @Test
    void create_shouldThrow_whenElectionIsNotOpen() {
        UUID electionId = UUID.randomUUID();

        Election election = buildElection(electionId, ElectionStatusCode.ACTIVE);
        Candidate candidate = new Candidate();
        Election candidateElection = new Election();
        candidateElection.setId(electionId);
        candidate.setElection(candidateElection);

        when(electionRepository.findById(electionId)).thenReturn(Optional.of(election));

        assertThatThrownBy(() -> candidateService.create(candidate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("OPEN elections");

        verify(candidateRepository, never()).save(any());
    }

    @Test
    void update_shouldThrow_whenExistingElectionIsNotOpen() {
        UUID candidateId = UUID.randomUUID();
        UUID electionId = UUID.randomUUID();

        Candidate existing = new Candidate();
        existing.setId(candidateId);
        existing.setElection(buildElection(electionId, ElectionStatusCode.CLOSED));

        Candidate update = new Candidate();
        update.setName("Updated");
        update.setDescription("desc");

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> candidateService.update(candidateId, update))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("OPEN elections");

        verify(candidateRepository, never()).save(any());
    }

    @Test
    void update_shouldThrow_whenTargetElectionNotFound() {
        UUID candidateId = UUID.randomUUID();
        UUID existingElectionId = UUID.randomUUID();
        UUID targetElectionId = UUID.randomUUID();

        Candidate existing = new Candidate();
        existing.setId(candidateId);
        existing.setElection(buildElection(existingElectionId, ElectionStatusCode.OPEN));

        Candidate update = new Candidate();
        Election targetElection = new Election();
        targetElection.setId(targetElectionId);
        update.setElection(targetElection);

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(existing));
        when(electionRepository.findById(targetElectionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> candidateService.update(candidateId, update))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(targetElectionId.toString());

        verify(candidateRepository, never()).save(any());
    }

    private Election buildElection(UUID electionId, ElectionStatusCode statusCode) {
        ElectionStatus status = new ElectionStatus();
        status.setId(statusCode.getId());
        status.setName(statusCode.name());

        Election election = new Election();
        election.setId(electionId);
        election.setStatus(status);
        return election;
    }
}
