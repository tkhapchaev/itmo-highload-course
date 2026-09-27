package ru.tkhapchaev.electionservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tkhapchaev.electionservice.entity.Candidate;
import ru.tkhapchaev.electionservice.entity.Election;
import ru.tkhapchaev.electionservice.entity.ElectionStatusCode;
import ru.tkhapchaev.electionservice.repository.CandidateRepository;
import ru.tkhapchaev.electionservice.repository.ElectionRepository;
import ru.tkhapchaev.electionservice.service.CandidateService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;

    @Override
    @Transactional
    public Candidate create(Candidate candidate) {
        if (candidate.getElection() == null || candidate.getElection().getId() == null) {
            throw new IllegalArgumentException("Election id is required");
        }

        Election election = getElection(candidate.getElection().getId());
        ensureOpenElection(election);

        candidate.setElection(election);
        return candidateRepository.save(candidate);
    }

    @Override
    public Candidate getById(UUID id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found: " + id));
    }

    @Override
    public List<Candidate> getAll(UUID electionId) {
        if (electionId == null) {
            return candidateRepository.findAll();
        }

        if (!electionRepository.existsById(electionId)) {
            throw new EntityNotFoundException("Election not found: " + electionId);
        }
        return candidateRepository.findByElection_Id(electionId);
    }

    @Override
    @Transactional
    public Candidate update(UUID id, Candidate candidate) {
        Candidate existing = getById(id);
        existing.setName(candidate.getName());
        existing.setDescription(candidate.getDescription());

        Election targetElection = existing.getElection();
        if (candidate.getElection() != null && candidate.getElection().getId() != null) {
            targetElection = getElection(candidate.getElection().getId());
        }
        ensureOpenElection(targetElection);
        existing.setElection(targetElection);

        return candidateRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Candidate existing = getById(id);
        candidateRepository.delete(existing);
    }

    private Election getElection(UUID electionId) {
        return electionRepository.findById(electionId)
                .orElseThrow(() -> new EntityNotFoundException("Election not found: " + electionId));
    }

    private void ensureOpenElection(Election election) {
        if (election.getStatusCode() != ElectionStatusCode.OPEN) {
            throw new IllegalStateException("Candidate registration is allowed only for OPEN elections");
        }
    }
}
