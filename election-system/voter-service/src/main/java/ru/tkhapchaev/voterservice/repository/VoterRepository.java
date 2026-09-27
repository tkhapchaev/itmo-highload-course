package ru.tkhapchaev.voterservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tkhapchaev.voterservice.entity.VoterEntity;

import java.util.List;
import java.util.UUID;

public interface VoterRepository extends JpaRepository<VoterEntity, UUID> {
    List<VoterEntity> findByElectionId(UUID electionId);

    boolean existsByUserIdAndElectionId(UUID userId, UUID electionId);

    long countByElectionId(UUID electionId);
}
