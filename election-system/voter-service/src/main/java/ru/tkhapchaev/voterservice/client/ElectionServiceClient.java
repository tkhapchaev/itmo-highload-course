package ru.tkhapchaev.voterservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.tkhapchaev.voterservice.dto.internal.ElectionStatusLookupResponse;

import java.util.UUID;

@FeignClient(name = "election-service", path = "/internal/elections")
public interface ElectionServiceClient {

    @GetMapping("/{id}/status")
    ElectionStatusLookupResponse getElectionStatus(@PathVariable("id") UUID electionId);
}
