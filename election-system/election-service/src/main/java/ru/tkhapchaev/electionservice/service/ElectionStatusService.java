package ru.tkhapchaev.electionservice.service;

import ru.tkhapchaev.electionservice.entity.ElectionStatus;

import java.util.List;

public interface ElectionStatusService {
    List<ElectionStatus> getAll();

    ElectionStatus getById(Integer id);
}
