package ru.tkhapchaev.electionservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tkhapchaev.electionservice.entity.Candidate;
import ru.tkhapchaev.electionservice.entity.Election;
import ru.tkhapchaev.electionservice.service.CandidateService;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidateController.class)
class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CandidateService candidateService;

    @Test
    void getById_shouldReturnCandidateWithLinks() throws Exception {
        Candidate candidate = buildCandidate();

        when(candidateService.getById(candidate.getId())).thenReturn(candidate);

        mockMvc.perform(get("/api/candidates/{id}", candidate.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(candidate.getId().toString()))
                .andExpect(jsonPath("$.electionId").value(candidate.getElection().getId().toString()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.candidates.href").exists())
                .andExpect(jsonPath("$._links.election.href").exists());
    }

    @Test
    void create_shouldReturnCreatedCandidate() throws Exception {
        Candidate candidate = buildCandidate();

        when(candidateService.create(any(Candidate.class))).thenReturn(candidate);

        String body = """
                {
                  "name": "Candidate #1",
                  "description": "desc",
                  "electionId": "%s"
                }
                """.formatted(candidate.getElection().getId());

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(candidate.getId().toString()))
                .andExpect(jsonPath("$.name").value("Candidate #1"));
    }

    private Candidate buildCandidate() {
        Election election = new Election();
        election.setId(UUID.randomUUID());

        Candidate candidate = new Candidate();
        candidate.setId(UUID.randomUUID());
        candidate.setName("Candidate #1");
        candidate.setDescription("desc");
        candidate.setElection(election);
        candidate.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        candidate.setUpdatedAt(Instant.parse("2026-01-02T00:00:00Z"));
        return candidate;
    }
}
