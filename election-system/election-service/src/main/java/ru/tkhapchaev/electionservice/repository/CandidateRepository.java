package ru.tkhapchaev.electionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tkhapchaev.electionservice.entity.Candidate;

import java.util.List;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {
    List<Candidate> findByElection_Id(UUID electionId);
}
