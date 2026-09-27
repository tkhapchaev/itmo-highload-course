package ru.tkhapchaev.electionservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tkhapchaev.electionservice.entity.ElectionStatus;
import ru.tkhapchaev.electionservice.repository.ElectionStatusRepository;
import ru.tkhapchaev.electionservice.service.ElectionStatusService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElectionStatusServiceImpl implements ElectionStatusService {

    private final ElectionStatusRepository electionStatusRepository;

    @Override
    public List<ElectionStatus> getAll() {
        return electionStatusRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public ElectionStatus getById(Integer id) {
        return electionStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Election status not found: " + id));
    }
}
