package ru.tkhapchaev.electionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tkhapchaev.electionservice.entity.ElectionStatus;

public interface ElectionStatusRepository extends JpaRepository<ElectionStatus, Integer> {
}
