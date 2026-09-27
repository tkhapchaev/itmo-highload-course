package ru.tkhapchaev.electionservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.tkhapchaev.electionservice.dto.ElectionTurnoutResponse;
import ru.tkhapchaev.electionservice.entity.VoteAudit;
import ru.tkhapchaev.electionservice.repository.VoteAuditRepository;
import ru.tkhapchaev.electionservice.service.ElectionService;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoteCreatedListener {

    private final VoteAuditRepository voteAuditRepository;
    private final ElectionService electionService;

    @KafkaListener(topics = "${app.kafka.vote-created-topic:vote-created}", groupId = "${spring.application.name}")
    @Transactional
    public void onVoteCreated(VoteCreatedEvent event) {
        if (voteAuditRepository.existsByVoteId(event.voteId())) {
            return;
        }

        ElectionTurnoutResponse turnout = electionService.getTurnout(event.electionId());

        VoteAudit audit = new VoteAudit();
        audit.setVoteId(event.voteId());
        audit.setElectionId(event.electionId());
        audit.setCandidateId(event.candidateId());
        audit.setVoterId(event.voterId());
        audit.setOccurredAt(event.createdAt() != null ? event.createdAt() : java.time.Instant.now());
        audit.setTurnoutAfterVote(turnout.turnout());
        audit.setQuorumReachedAfterVote(turnout.quorumReached());

        voteAuditRepository.save(audit);
        log.info("Vote event stored: {}", event.voteId());
    }
}

