package ru.tkhapchaev.voteservice.service.impl;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tkhapchaev.voteservice.client.ElectionServiceClient;
import ru.tkhapchaev.voteservice.client.VoterServiceClient;
import ru.tkhapchaev.voteservice.dto.internal.CandidateLookupResponse;
import ru.tkhapchaev.voteservice.dto.internal.ElectionStatusLookupResponse;
import ru.tkhapchaev.voteservice.dto.internal.VoterLookupResponse;
import ru.tkhapchaev.voteservice.entity.VoteEntity;
import ru.tkhapchaev.voteservice.repository.VoteRepository;
import ru.tkhapchaev.voteservice.service.VoteService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final ElectionServiceClient electionServiceClient;
    private final VoterServiceClient voterServiceClient;

    @Override
    @Transactional
    public VoteEntity create(VoteEntity vote) {
        UUID candidateId = vote.getCandidateId();
        UUID voterId = vote.getVoterId();

        if (voteRepository.existsByVoterId(voterId)) {
            throw new IllegalStateException("This voter has already voted");
        }

        CandidateLookupResponse candidate = getCandidate(candidateId);
        VoterLookupResponse voter = getVoter(voterId);

        if (!candidate.electionId().equals(voter.electionId())) {
            throw new IllegalStateException("Voter and candidate belong to different elections");
        }

        ElectionStatusLookupResponse status = getElectionStatus(candidate.electionId());
        if (!"ACTIVE".equals(status.statusCode())) {
            throw new IllegalStateException("Voting is allowed only for ACTIVE elections");
        }

        if (voteRepository.existsByUserIdAndElectionId(voter.userId(), candidate.electionId())) {
            throw new IllegalStateException("User has already voted in this election");
        }

        vote.setElectionId(candidate.electionId());
        vote.setUserId(voter.userId());

        return voteRepository.save(vote);
    }

    @Override
    public VoteEntity getById(UUID id) {
        return voteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vote not found: " + id));
    }

    @Override
    public List<VoteEntity> getAll(UUID candidateId, UUID userId) {
        if (candidateId != null) {
            return voteRepository.findByCandidateId(candidateId);
        }

        if (userId != null) {
            return voteRepository.findByUserId(userId);
        }

        return voteRepository.findAll();
    }

    @Override
    @Transactional
    public VoteEntity update(UUID id, VoteEntity vote) {
        VoteEntity existing = getById(id);

        UUID candidateId = vote.getCandidateId();
        UUID voterId = vote.getVoterId();

        if (!existing.getVoterId().equals(voterId) && voteRepository.existsByVoterId(voterId)) {
            throw new IllegalStateException("This voter has already voted");
        }

        CandidateLookupResponse candidate = getCandidate(candidateId);
        VoterLookupResponse voter = getVoter(voterId);

        if (!candidate.electionId().equals(voter.electionId())) {
            throw new IllegalStateException("Voter and candidate belong to different elections");
        }

        ElectionStatusLookupResponse status = getElectionStatus(candidate.electionId());
        if (!"ACTIVE".equals(status.statusCode())) {
            throw new IllegalStateException("Voting is allowed only for ACTIVE elections");
        }

        existing.setCandidateId(candidateId);
        existing.setVoterId(voterId);
        existing.setElectionId(candidate.electionId());
        existing.setUserId(voter.userId());

        return voteRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        voteRepository.delete(getById(id));
    }

    @Override
    public long countByElectionId(UUID electionId) {
        return voteRepository.countByElectionId(electionId);
    }

    private CandidateLookupResponse getCandidate(UUID candidateId) {
        try {
            return electionServiceClient.getCandidate(candidateId);
        } catch (FeignException.NotFound ex) {
            throw new EntityNotFoundException("Candidate not found: " + candidateId);
        }
    }

    private VoterLookupResponse getVoter(UUID voterId) {
        try {
            return voterServiceClient.getVoter(voterId);
        } catch (FeignException.NotFound ex) {
            throw new EntityNotFoundException("Voter not found: " + voterId);
        }
    }

    private ElectionStatusLookupResponse getElectionStatus(UUID electionId) {
        try {
            return electionServiceClient.getElectionStatus(electionId);
        } catch (FeignException.NotFound ex) {
            throw new EntityNotFoundException("Election not found: " + electionId);
        }
    }
}
