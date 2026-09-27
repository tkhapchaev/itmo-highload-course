package ru.tkhapchaev.electionservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tkhapchaev.electionservice.entity.Election;
import ru.tkhapchaev.electionservice.entity.ElectionStatus;
import ru.tkhapchaev.electionservice.service.ElectionService;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ElectionController.class)
class ElectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElectionService electionService;

    @Test
    void getById_shouldReturnElectionWithHateoasLinks() throws Exception {
        UUID id = UUID.randomUUID();
        Election election = buildElection(id);

        when(electionService.getById(id)).thenReturn(election);

        mockMvc.perform(get("/api/elections/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.turnout.href").exists())
                .andExpect(jsonPath("$._links.candidates.href").exists());
    }

    @Test
    void create_shouldReturnCreatedElection() throws Exception {
        UUID id = UUID.randomUUID();
        Election election = buildElection(id);

        when(electionService.create(any(Election.class), eq(1))).thenReturn(election);

        String body = """
                {
                  "name": "Test election",
                  "description": "desc",
                  "statusId": 1,
                  "quorum": 55.0
                }
                """;

        mockMvc.perform(post("/api/elections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    private Election buildElection(UUID id) {
        ElectionStatus status = new ElectionStatus();
        status.setId(1);
        status.setName("ACTIVE");

        Election election = new Election();
        election.setId(id);
        election.setName("Election");
        election.setDescription("desc");
        election.setStatus(status);
        election.setQuorum(50.0);
        election.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        election.setUpdatedAt(Instant.parse("2026-01-02T00:00:00Z"));

        return election;
    }
}
