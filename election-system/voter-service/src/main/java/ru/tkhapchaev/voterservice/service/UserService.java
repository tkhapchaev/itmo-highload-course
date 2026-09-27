package ru.tkhapchaev.voterservice.service;

import ru.tkhapchaev.voterservice.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserEntity create(UserEntity user);

    UserEntity getById(UUID id);

    List<UserEntity> getAll();

    UserEntity update(UUID id, UserEntity user);

    void delete(UUID id);
}
