package ru.tkhapchaev.voterservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tkhapchaev.voterservice.entity.UserEntity;
import ru.tkhapchaev.voterservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_shouldSaveUser() {
        UserEntity user = user("Name", "desc");
        when(userRepository.save(user)).thenReturn(user);

        UserEntity result = userService.create(user);

        assertThat(result).isEqualTo(user);
        verify(userRepository).save(user);
    }

    @Test
    void getById_shouldReturnUser() {
        UUID id = UUID.randomUUID();
        UserEntity user = user("Name", "desc");
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserEntity result = userService.getById(id);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getById_shouldThrow_whenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        List<UserEntity> users = List.of(user("u1", "d1"), user("u2", "d2"));
        when(userRepository.findAll()).thenReturn(users);

        List<UserEntity> result = userService.getAll();

        assertThat(result).isEqualTo(users);
    }

    @Test
    void update_shouldModifyAndSaveExistingUser() {
        UUID id = UUID.randomUUID();

        UserEntity existing = user("Old", "old");
        existing.setId(id);

        UserEntity update = user("New", "new");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        UserEntity result = userService.update(id, update);

        assertThat(result).isEqualTo(existing);
        assertThat(existing.getName()).isEqualTo("New");
        assertThat(existing.getDescription()).isEqualTo("new");
        verify(userRepository).save(existing);
    }

    @Test
    void update_shouldThrow_whenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(id, user("N", "D")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void delete_shouldDeleteExistingUser() {
        UUID id = UUID.randomUUID();
        UserEntity user = user("Name", "desc");
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.delete(id);

        verify(userRepository).delete(user);
    }

    private static UserEntity user(String name, String description) {
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setDescription(description);
        return user;
    }
}
