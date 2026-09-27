package ru.tkhapchaev.electionservice.service;

import ru.tkhapchaev.electionservice.dto.ElectionTurnoutResponse;
import ru.tkhapchaev.electionservice.entity.Election;

import java.util.List;
import java.util.UUID;

public interface ElectionService {
    Election create(Election election, Integer statusId);

    Election getById(UUID id);

    List<Election> getAll(Integer statusId);

    Election update(UUID id, Election election, Integer statusId);

    void delete(UUID id);

    ElectionTurnoutResponse getTurnout(UUID electionId);
}
