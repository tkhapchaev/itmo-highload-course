package ru.tkhapchaev.electionservice.controller.internal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tkhapchaev.electionservice.entity.Election;
import ru.tkhapchaev.electionservice.entity.ElectionStatus;
import ru.tkhapchaev.electionservice.service.ElectionService;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ElectionInternalController.class)
class ElectionInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElectionService electionService;

    @Test
    void getStatus_shouldReturnElectionStatusLookup() throws Exception {
        UUID electionId = UUID.randomUUID();

        ElectionStatus status = new ElectionStatus();
        status.setId(1);
        status.setName("ACTIVE");

        Election election = new Election();
        election.setId(electionId);
        election.setStatus(status);

        when(electionService.getById(electionId)).thenReturn(election);

        mockMvc.perform(get("/internal/elections/{id}/status", electionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusId").value(1))
                .andExpect(jsonPath("$.statusCode").value("ACTIVE"));
    }
}
