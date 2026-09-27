package ru.tkhapchaev.electionservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tkhapchaev.electionservice.client.VoteServiceClient;
import ru.tkhapchaev.electionservice.client.VoterServiceClient;
import ru.tkhapchaev.electionservice.dto.CountResponse;
import ru.tkhapchaev.electionservice.dto.ElectionTurnoutResponse;
import ru.tkhapchaev.electionservice.entity.Election;
import ru.tkhapchaev.electionservice.entity.ElectionStatus;
import ru.tkhapchaev.electionservice.repository.ElectionRepository;
import ru.tkhapchaev.electionservice.repository.ElectionStatusRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElectionServiceImplTest {

    @Mock
    private ElectionRepository electionRepository;

    @Mock
    private ElectionStatusRepository electionStatusRepository;

    @Mock
    private VoteServiceClient voteServiceClient;

    @Mock
    private VoterServiceClient voterServiceClient;

    @InjectMocks
    private ElectionServiceImpl electionService;

    @Test
    void create_shouldSetStatusAndSaveElection() {
        Election election = new Election();
        ElectionStatus status = status(1, "ACTIVE");

        when(electionStatusRepository.findById(1)).thenReturn(Optional.of(status));
        when(electionRepository.save(election)).thenReturn(election);

        Election result = electionService.create(election, 1);

        assertThat(result).isEqualTo(election);
        assertThat(election.getStatus()).isEqualTo(status);
        verify(electionRepository).save(election);
    }

    @Test
    void create_shouldThrow_whenStatusNotFound() {
        Election election = new Election();
        when(electionStatusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> electionService.create(election, 99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Election status not found");
    }

    @Test
    void getById_shouldReturnElection() {
        UUID id = UUID.randomUUID();
        Election election = new Election();
        election.setId(id);

        when(electionRepository.findById(id)).thenReturn(Optional.of(election));

        Election result = electionService.getById(id);

        assertThat(result).isEqualTo(election);
    }

    @Test
    void getById_shouldThrow_whenElectionNotFound() {
        UUID id = UUID.randomUUID();
        when(electionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> electionService.getById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void getAll_shouldReturnAll_whenStatusFilterIsNull() {
        List<Election> elections = List.of(new Election(), new Election());
        when(electionRepository.findAll()).thenReturn(elections);

        List<Election> result = electionService.getAll(null);

        assertThat(result).isEqualTo(elections);
    }

    @Test
    void getAll_shouldReturnFilteredByStatus_whenStatusFilterProvided() {
        List<Election> elections = List.of(new Election());
        when(electionRepository.findByStatus_Id(1)).thenReturn(elections);

        List<Election> result = electionService.getAll(1);

        assertThat(result).isEqualTo(elections);
    }

    @Test
    void update_shouldModifyElectionAndSave() {
        UUID id = UUID.randomUUID();

        Election existing = election(id, "Old", "old-desc", 40.0, status(0, "OPEN"));
        Election update = election(null, "New", "new-desc", 60.0, null);
        ElectionStatus newStatus = status(1, "ACTIVE");

        when(electionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(electionStatusRepository.findById(1)).thenReturn(Optional.of(newStatus));
        when(electionRepository.save(existing)).thenReturn(existing);

        Election result = electionService.update(id, update, 1);

        assertThat(result).isEqualTo(existing);
        assertThat(existing.getName()).isEqualTo("New");
        assertThat(existing.getDescription()).isEqualTo("new-desc");
        assertThat(existing.getQuorum()).isEqualTo(60.0);
        assertThat(existing.getStatus()).isEqualTo(newStatus);
    }

    @Test
    void delete_shouldDeleteExistingElection() {
        UUID id = UUID.randomUUID();
        Election existing = election(id, "Name", "desc", 50.0, status(0, "OPEN"));

        when(electionRepository.findById(id)).thenReturn(Optional.of(existing));

        electionService.delete(id);

        verify(electionRepository).delete(existing);
    }

    @Test
    void getTurnout_shouldCalculateTurnoutAndQuorumFlag() {
        UUID electionId = UUID.randomUUID();
        Election election = election(electionId, "E", "d", 60.0, status(1, "ACTIVE"));

        when(electionRepository.findById(electionId)).thenReturn(Optional.of(election));
        when(voterServiceClient.countByElectionId(electionId)).thenReturn(new CountResponse(10));
        when(voteServiceClient.countByElectionId(electionId)).thenReturn(new CountResponse(7));

        ElectionTurnoutResponse result = electionService.getTurnout(electionId);

        assertThat(result.electionId()).isEqualTo(electionId);
        assertThat(result.voters()).isEqualTo(10);
        assertThat(result.votes()).isEqualTo(7);
        assertThat(result.turnout()).isEqualTo(70.0);
        assertThat(result.quorum()).isEqualTo(60.0);
        assertThat(result.quorumReached()).isTrue();
    }

    @Test
    void getTurnout_shouldReturnZero_whenNoVoters() {
        UUID electionId = UUID.randomUUID();
        Election election = election(electionId, "E", "d", 50.0, status(1, "ACTIVE"));

        when(electionRepository.findById(electionId)).thenReturn(Optional.of(election));
        when(voterServiceClient.countByElectionId(electionId)).thenReturn(new CountResponse(0));
        when(voteServiceClient.countByElectionId(electionId)).thenReturn(new CountResponse(3));

        ElectionTurnoutResponse result = electionService.getTurnout(electionId);

        assertThat(result.turnout()).isEqualTo(0.0);
        assertThat(result.quorumReached()).isFalse();
    }

    private static Election election(UUID id, String name, String description, double quorum, ElectionStatus status) {
        Election election = new Election();
        election.setId(id);
        election.setName(name);
        election.setDescription(description);
        election.setQuorum(quorum);
        election.setStatus(status);
        return election;
    }

    private static ElectionStatus status(int id, String name) {
        ElectionStatus status = new ElectionStatus();
        status.setId(id);
        status.setName(name);
        return status;
    }
}
