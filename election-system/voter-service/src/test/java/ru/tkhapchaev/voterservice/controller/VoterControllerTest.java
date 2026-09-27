package ru.tkhapchaev.voterservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tkhapchaev.voterservice.entity.VoterEntity;
import ru.tkhapchaev.voterservice.service.VoterService;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoterController.class)
class VoterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoterService voterService;

    @Test
    void getById_shouldReturnVoterWithLinks() throws Exception {
        VoterEntity voter = buildVoter();

        when(voterService.getById(voter.getId())).thenReturn(voter);

        mockMvc.perform(get("/api/voters/{id}", voter.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(voter.getId().toString()))
                .andExpect(jsonPath("$.userId").value(voter.getUserId().toString()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.voters.href").exists());
    }

    @Test
    void create_shouldReturnCreatedVoter() throws Exception {
        VoterEntity voter = buildVoter();

        when(voterService.create(any(VoterEntity.class))).thenReturn(voter);

        String body = """
                {
                  "userId": "%s",
                  "electionId": "%s"
                }
                """.formatted(voter.getUserId(), voter.getElectionId());

        mockMvc.perform(post("/api/voters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(voter.getId().toString()))
                .andExpect(jsonPath("$.electionId").value(voter.getElectionId().toString()));
    }

    private VoterEntity buildVoter() {
        VoterEntity voter = new VoterEntity();
        voter.setId(UUID.randomUUID());
        voter.setUserId(UUID.randomUUID());
        voter.setElectionId(UUID.randomUUID());
        voter.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        return voter;
    }
}
