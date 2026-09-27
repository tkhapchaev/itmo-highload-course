package ru.tkhapchaev.voteservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.tkhapchaev.voteservice.dto.internal.CandidateLookupResponse;
import ru.tkhapchaev.voteservice.dto.internal.ElectionStatusLookupResponse;

import java.util.UUID;

@FeignClient(name = "election-service")
public interface ElectionServiceClient {

    @GetMapping("/internal/candidates/{id}")
    CandidateLookupResponse getCandidate(@PathVariable("id") UUID candidateId);

    @GetMapping("/internal/elections/{id}/status")
    ElectionStatusLookupResponse getElectionStatus(@PathVariable("id") UUID electionId);
}
