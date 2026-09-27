package ru.tkhapchaev.electionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tkhapchaev.electionservice.entity.VoteAudit;

import java.util.UUID;

public interface VoteAuditRepository extends JpaRepository<VoteAudit, UUID> {
    boolean existsByVoteId(UUID voteId);
}
