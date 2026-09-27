package ru.tkhapchaev.voterservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tkhapchaev.voterservice.entity.UserEntity;
import ru.tkhapchaev.voterservice.service.UserService;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getById_shouldReturnUserWithLinks() throws Exception {
        UUID id = UUID.randomUUID();
        UserEntity user = buildUser(id);

        when(userService.getById(id)).thenReturn(user);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.users.href").exists());
    }

    @Test
    void create_shouldReturnCreatedUser() throws Exception {
        UUID id = UUID.randomUUID();
        UserEntity user = buildUser(id);

        when(userService.create(any(UserEntity.class))).thenReturn(user);

        String body = """
                {
                  "name": "Test user",
                  "description": "desc"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Test user"));
    }

    private UserEntity buildUser(UUID id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setName("Test user");
        user.setDescription("desc");
        user.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        user.setUpdatedAt(Instant.parse("2026-01-01T01:00:00Z"));
        return user;
    }
}
