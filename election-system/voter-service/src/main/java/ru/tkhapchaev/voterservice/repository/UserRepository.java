package ru.tkhapchaev.voterservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tkhapchaev.voterservice.entity.UserEntity;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
