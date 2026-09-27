package ru.tkhapchaev.voterservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tkhapchaev.voterservice.client.ElectionServiceClient;
import ru.tkhapchaev.voterservice.dto.internal.ElectionStatusLookupResponse;
import ru.tkhapchaev.voterservice.entity.VoterEntity;
import ru.tkhapchaev.voterservice.repository.UserRepository;
import ru.tkhapchaev.voterservice.repository.VoterRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoterServiceImplTest {

    @Mock
    private VoterRepository voterRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ElectionServiceClient electionServiceClient;

    @InjectMocks
    private VoterServiceImpl voterService;

    @Test
    void update_shouldSave_whenElectionIsOpen() {
        UUID voterId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID electionId = UUID.randomUUID();

        VoterEntity existing = new VoterEntity();
        existing.setId(voterId);

        VoterEntity update = new VoterEntity();
        update.setUserId(userId);
        update.setElectionId(electionId);

        when(voterRepository.findById(voterId)).thenReturn(Optional.of(existing));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(electionServiceClient.getElectionStatus(electionId))
                .thenReturn(new ElectionStatusLookupResponse(0, "OPEN"));
        when(voterRepository.save(existing)).thenReturn(existing);

        VoterEntity result = voterService.update(voterId, update);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getElectionId()).isEqualTo(electionId);
        verify(voterRepository).save(existing);
    }

    @Test
    void update_shouldThrow_whenElectionIsNotOpen() {
        UUID voterId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID electionId = UUID.randomUUID();

        VoterEntity existing = new VoterEntity();
        existing.setId(voterId);

        VoterEntity update = new VoterEntity();
        update.setUserId(userId);
        update.setElectionId(electionId);

        when(voterRepository.findById(voterId)).thenReturn(Optional.of(existing));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(electionServiceClient.getElectionStatus(electionId))
                .thenReturn(new ElectionStatusLookupResponse(1, "ACTIVE"));

        assertThatThrownBy(() -> voterService.update(voterId, update))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("OPEN elections");

        verify(voterRepository, never()).save(any());
    }

    @Test
    void update_shouldThrow_whenUserNotFound() {
        UUID voterId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID electionId = UUID.randomUUID();

        VoterEntity existing = new VoterEntity();
        existing.setId(voterId);

        VoterEntity update = new VoterEntity();
        update.setUserId(userId);
        update.setElectionId(electionId);

        when(voterRepository.findById(voterId)).thenReturn(Optional.of(existing));
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> voterService.update(voterId, update))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(userId.toString());

        verify(voterRepository, never()).save(any());
    }
}
