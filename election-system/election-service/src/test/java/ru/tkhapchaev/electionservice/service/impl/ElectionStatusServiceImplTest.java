package ru.tkhapchaev.electionservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.tkhapchaev.electionservice.entity.ElectionStatus;
import ru.tkhapchaev.electionservice.repository.ElectionStatusRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElectionStatusServiceImplTest {

    @Mock
    private ElectionStatusRepository electionStatusRepository;

    @InjectMocks
    private ElectionStatusServiceImpl electionStatusService;

    @Test
    void getAll_shouldReturnStatusesSortedByIdAsc() {
        ElectionStatus open = status(0, "OPEN");
        ElectionStatus active = status(1, "ACTIVE");

        when(electionStatusRepository.findAll(Sort.by(Sort.Direction.ASC, "id")))
                .thenReturn(List.of(open, active));

        List<ElectionStatus> result = electionStatusService.getAll();

        assertThat(result).containsExactly(open, active);
        verify(electionStatusRepository).findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Test
    void getById_shouldReturnStatus() {
        ElectionStatus status = status(2, "CLOSED");
        when(electionStatusRepository.findById(2)).thenReturn(Optional.of(status));

        ElectionStatus result = electionStatusService.getById(2);

        assertThat(result).isEqualTo(status);
    }

    @Test
    void getById_shouldThrow_whenStatusNotFound() {
        when(electionStatusRepository.findById(77)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> electionStatusService.getById(77))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Election status not found");
    }

    private static ElectionStatus status(int id, String name) {
        ElectionStatus status = new ElectionStatus();
        status.setId(id);
        status.setName(name);
        return status;
    }
}
