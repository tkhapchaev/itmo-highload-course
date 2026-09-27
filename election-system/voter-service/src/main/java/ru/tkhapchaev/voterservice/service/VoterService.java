package ru.tkhapchaev.voterservice.service;

import ru.tkhapchaev.voterservice.entity.VoterEntity;

import java.util.List;
import java.util.UUID;

public interface VoterService {
    VoterEntity create(VoterEntity voter);

    VoterEntity getById(UUID id);

    List<VoterEntity> getAll(UUID electionId);

    VoterEntity update(UUID id, VoterEntity voter);

    void delete(UUID id);

    long countByElectionId(UUID electionId);
}
