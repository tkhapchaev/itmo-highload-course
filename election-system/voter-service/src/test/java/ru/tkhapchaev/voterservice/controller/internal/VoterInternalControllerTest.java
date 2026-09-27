package ru.tkhapchaev.voterservice.controller.internal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tkhapchaev.voterservice.entity.VoterEntity;
import ru.tkhapchaev.voterservice.service.VoterService;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoterInternalController.class)
class VoterInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoterService voterService;

    @Test
    void getById_shouldReturnLookupResponse() throws Exception {
        UUID voterId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID electionId = UUID.randomUUID();

        VoterEntity voter = new VoterEntity();
        voter.setId(voterId);
        voter.setUserId(userId);
        voter.setElectionId(electionId);

        when(voterService.getById(voterId)).thenReturn(voter);

        mockMvc.perform(get("/internal/voters/{id}", voterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voterId").value(voterId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.electionId").value(electionId.toString()));
    }

    @Test
    void countByElectionId_shouldReturnCount() throws Exception {
        UUID electionId = UUID.randomUUID();
        when(voterService.countByElectionId(electionId)).thenReturn(5L);

        mockMvc.perform(get("/internal/voters/count").param("electionId", electionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
    }
}
