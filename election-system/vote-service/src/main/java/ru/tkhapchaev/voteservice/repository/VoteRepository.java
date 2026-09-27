package ru.tkhapchaev.voteservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tkhapchaev.voteservice.entity.VoteEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<VoteEntity, UUID> {
    long countByElectionId(UUID electionId);

    long countByCandidateId(UUID candidateId);

    boolean existsByVoterId(UUID voterId);

    boolean existsByUserIdAndElectionId(UUID userId, UUID electionId);

    List<VoteEntity> findByCandidateId(UUID candidateId);

    List<VoteEntity> findByUserId(UUID userId);

    Optional<VoteEntity> findByVoterId(UUID voterId);
}
