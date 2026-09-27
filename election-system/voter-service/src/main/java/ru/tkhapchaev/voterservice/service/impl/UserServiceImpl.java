package ru.tkhapchaev.voterservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tkhapchaev.voterservice.entity.UserEntity;
import ru.tkhapchaev.voterservice.repository.UserRepository;
import ru.tkhapchaev.voterservice.service.UserService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserEntity create(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public UserEntity getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    @Override
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public UserEntity update(UUID id, UserEntity user) {
        UserEntity existing = getById(id);
        existing.setName(user.getName());
        existing.setDescription(user.getDescription());
        return userRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        userRepository.delete(getById(id));
    }
}
