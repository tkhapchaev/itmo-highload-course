package ru.tkhapchaev.electionservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tkhapchaev.electionservice.dto.ElectionTurnoutResponse;
import ru.tkhapchaev.electionservice.client.VoteServiceClient;
import ru.tkhapchaev.electionservice.client.VoterServiceClient;
import ru.tkhapchaev.electionservice.dto.CountResponse;
import ru.tkhapchaev.electionservice.entity.Election;
import ru.tkhapchaev.electionservice.entity.ElectionStatus;
import ru.tkhapchaev.electionservice.repository.ElectionRepository;
import ru.tkhapchaev.electionservice.repository.ElectionStatusRepository;
import ru.tkhapchaev.electionservice.service.ElectionService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;
    private final ElectionStatusRepository electionStatusRepository;
    private final VoteServiceClient voteServiceClient;
    private final VoterServiceClient voterServiceClient;

    @Override
    @Transactional
    public Election create(Election election, Integer statusId) {
        election.setStatus(getStatus(statusId));
        return electionRepository.save(election);
    }

    @Override
    public Election getById(UUID id) {
        return electionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Election not found: " + id));
    }

    @Override
    public List<Election> getAll(Integer statusId) {
        if (statusId == null) {
            return electionRepository.findAll();
        }
        return electionRepository.findByStatus_Id(statusId);
    }

    @Override
    @Transactional
    public Election update(UUID id, Election election, Integer statusId) {
        Election existing = getById(id);
        existing.setName(election.getName());
        existing.setDescription(election.getDescription());
        existing.setQuorum(election.getQuorum());
        existing.setStatus(getStatus(statusId));
        return electionRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Election existing = getById(id);
        electionRepository.delete(existing);
    }

    @Override
    public ElectionTurnoutResponse getTurnout(UUID electionId) {
        Election election = getById(electionId);

        CountResponse votersResponse = voterServiceClient.countByElectionId(electionId);
        CountResponse votesResponse = voteServiceClient.countByElectionId(electionId);

        long voters = votersResponse.count();
        long votes = votesResponse.count();

        double turnout = voters == 0 ? 0.0 : votes * 100.0 / voters;

        return new ElectionTurnoutResponse(
                electionId,
                voters,
                votes,
                turnout,
                election.getQuorum(),
                turnout >= election.getQuorum()
        );
    }

    private ElectionStatus getStatus(Integer statusId) {
        return electionStatusRepository.findById(statusId)
                .orElseThrow(() -> new EntityNotFoundException("Election status not found: " + statusId));
    }
}
