package ru.tkhapchaev.electionservice.controller.internal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tkhapchaev.electionservice.entity.Candidate;
import ru.tkhapchaev.electionservice.entity.Election;
import ru.tkhapchaev.electionservice.service.CandidateService;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidateInternalController.class)
class CandidateInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CandidateService candidateService;

    @Test
    void getCandidate_shouldReturnLookupResponse() throws Exception {
        UUID candidateId = UUID.randomUUID();
        UUID electionId = UUID.randomUUID();

        Election election = new Election();
        election.setId(electionId);

        Candidate candidate = new Candidate();
        candidate.setId(candidateId);
        candidate.setElection(election);

        when(candidateService.getById(candidateId)).thenReturn(candidate);

        mockMvc.perform(get("/internal/candidates/{id}", candidateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidateId").value(candidateId.toString()))
                .andExpect(jsonPath("$.electionId").value(electionId.toString()));
    }
}
