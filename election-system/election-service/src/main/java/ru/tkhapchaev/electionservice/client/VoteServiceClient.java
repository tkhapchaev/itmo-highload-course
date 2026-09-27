package ru.tkhapchaev.electionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tkhapchaev.electionservice.dto.CountResponse;

import java.util.UUID;

@FeignClient(name = "vote-service", path = "/internal/votes")
public interface VoteServiceClient {

    @GetMapping("/count")
    CountResponse countByElectionId(@RequestParam("electionId") UUID electionId);
}
