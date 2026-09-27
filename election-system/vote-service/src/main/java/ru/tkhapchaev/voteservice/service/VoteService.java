package ru.tkhapchaev.voteservice.service;

import ru.tkhapchaev.voteservice.entity.VoteEntity;

import java.util.List;
import java.util.UUID;

public interface VoteService {
    VoteEntity create(VoteEntity vote);

    VoteEntity getById(UUID id);

    List<VoteEntity> getAll(UUID candidateId, UUID userId);

    VoteEntity update(UUID id, VoteEntity vote);

    void delete(UUID id);

    long countByElectionId(UUID electionId);
}
