package ru.tkhapchaev.voterservice.service.impl;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tkhapchaev.voterservice.client.ElectionServiceClient;
import ru.tkhapchaev.voterservice.dto.internal.ElectionStatusLookupResponse;
import ru.tkhapchaev.voterservice.entity.VoterEntity;
import ru.tkhapchaev.voterservice.repository.UserRepository;
import ru.tkhapchaev.voterservice.repository.VoterRepository;
import ru.tkhapchaev.voterservice.service.VoterService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoterServiceImpl implements VoterService {

    private final VoterRepository voterRepository;
    private final UserRepository userRepository;
    private final ElectionServiceClient electionServiceClient;

    @Override
    @Transactional
    public VoterEntity create(VoterEntity voter) {
        UUID userId = voter.getUserId();
        UUID electionId = voter.getElectionId();

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found: " + userId);
        }

        ensureOpenElection(electionId);

        if (voterRepository.existsByUserIdAndElectionId(userId, electionId)) {
            throw new IllegalStateException("User is already registered for this election");
        }

        return voterRepository.save(voter);
    }

    @Override
    public VoterEntity getById(UUID id) {
        return voterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Voter not found: " + id));
    }

    @Override
    public List<VoterEntity> getAll(UUID electionId) {
        if (electionId == null) {
            return voterRepository.findAll();
        }
        return voterRepository.findByElectionId(electionId);
    }

    @Override
    @Transactional
    public VoterEntity update(UUID id, VoterEntity voter) {
        VoterEntity existing = getById(id);

        if (!userRepository.existsById(voter.getUserId())) {
            throw new EntityNotFoundException("User not found: " + voter.getUserId());
        }

        ensureOpenElection(voter.getElectionId());

        existing.setUserId(voter.getUserId());
        existing.setElectionId(voter.getElectionId());
        return voterRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        voterRepository.delete(getById(id));
    }

    @Override
    public long countByElectionId(UUID electionId) {
        return voterRepository.countByElectionId(electionId);
    }

    private void ensureOpenElection(UUID electionId) {
        ElectionStatusLookupResponse status;
        try {
            status = electionServiceClient.getElectionStatus(electionId);
        } catch (FeignException.NotFound ex) {
            throw new EntityNotFoundException("Election not found: " + electionId);
        }

        if (!"OPEN".equals(status.statusCode())) {
            throw new IllegalStateException("Voter registration is allowed only for OPEN elections");
        }
    }
}
