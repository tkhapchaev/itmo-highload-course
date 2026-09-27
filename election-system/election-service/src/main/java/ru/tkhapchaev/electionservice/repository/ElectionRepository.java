package ru.tkhapchaev.electionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tkhapchaev.electionservice.entity.Election;

import java.util.List;
import java.util.UUID;

public interface ElectionRepository extends JpaRepository<Election, UUID> {
    List<Election> findByStatus_Id(Integer statusId);
}
