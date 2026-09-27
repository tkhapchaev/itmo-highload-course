package ru.tkhapchaev.voteservice.controller.internal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tkhapchaev.voteservice.service.VoteService;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteInternalController.class)
class VoteInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    @Test
    void countByElectionId_shouldReturnCount() throws Exception {
        UUID electionId = UUID.randomUUID();
        when(voteService.countByElectionId(electionId)).thenReturn(7L);

        mockMvc.perform(get("/internal/votes/count").param("electionId", electionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(7));
    }
}
