package ru.tkhapchaev.voteservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tkhapchaev.voteservice.entity.VoteEntity;
import ru.tkhapchaev.voteservice.service.VoteService;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteController.class)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    @Test
    void getById_shouldReturnVoteWithLinks() throws Exception {
        VoteEntity vote = buildVote();

        when(voteService.getById(vote.getId())).thenReturn(vote);

        mockMvc.perform(get("/api/votes/{id}", vote.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vote.getId().toString()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.votes.href").exists());
    }

    @Test
    void create_shouldReturnCreatedVote() throws Exception {
        VoteEntity vote = buildVote();

        when(voteService.create(any(VoteEntity.class))).thenReturn(vote);

        String body = """
                {
                  "candidateId": "%s",
                  "voterId": "%s"
                }
                """.formatted(vote.getCandidateId(), vote.getVoterId());

        mockMvc.perform(post("/api/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(vote.getId().toString()))
                .andExpect(jsonPath("$.candidateId").value(vote.getCandidateId().toString()));
    }

    @Test
    void delete_shouldRevokeVote() throws Exception {
        UUID voteId = UUID.randomUUID();

        mockMvc.perform(delete("/api/votes/{id}", voteId))
                .andExpect(status().isNoContent());

        verify(voteService).delete(voteId);
    }

    @Test
    void update_shouldNotBeSupported() throws Exception {
        VoteEntity vote = buildVote();
        String body = """
                {
                  "candidateId": "%s",
                  "voterId": "%s"
                }
                """.formatted(vote.getCandidateId(), vote.getVoterId());

        mockMvc.perform(put("/api/votes/{id}", vote.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isMethodNotAllowed());
    }

    private VoteEntity buildVote() {
        VoteEntity vote = new VoteEntity();
        vote.setId(UUID.randomUUID());
        vote.setCandidateId(UUID.randomUUID());
        vote.setVoterId(UUID.randomUUID());
        vote.setElectionId(UUID.randomUUID());
        vote.setUserId(UUID.randomUUID());
        vote.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        return vote;
    }
}
