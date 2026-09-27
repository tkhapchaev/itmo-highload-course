package ru.tkhapchaev.electionservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tkhapchaev.electionservice.entity.ElectionStatus;
import ru.tkhapchaev.electionservice.service.ElectionStatusService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ElectionStatusController.class)
class ElectionStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElectionStatusService electionStatusService;

    @Test
    void getAll_shouldReturnStatuses() throws Exception {
        ElectionStatus open = new ElectionStatus();
        open.setId(0);
        open.setName("OPEN");

        ElectionStatus active = new ElectionStatus();
        active.setId(1);
        active.setName("ACTIVE");

        when(electionStatusService.getAll()).thenReturn(List.of(open, active));

        mockMvc.perform(get("/api/election-statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.electionStatusResponseList[0].id").value(0))
                .andExpect(jsonPath("$._embedded.electionStatusResponseList[0].name").value("OPEN"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }
}
