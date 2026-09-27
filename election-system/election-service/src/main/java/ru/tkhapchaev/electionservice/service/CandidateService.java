package ru.tkhapchaev.electionservice.service;

import ru.tkhapchaev.electionservice.entity.Candidate;

import java.util.List;
import java.util.UUID;

public interface CandidateService {
    Candidate create(Candidate candidate);

    Candidate getById(UUID id);

    List<Candidate> getAll(UUID electionId);

    Candidate update(UUID id, Candidate candidate);

    void delete(UUID id);
}
