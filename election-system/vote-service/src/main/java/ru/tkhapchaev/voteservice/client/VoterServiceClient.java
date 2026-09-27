package ru.tkhapchaev.voteservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.tkhapchaev.voteservice.dto.internal.VoterLookupResponse;

import java.util.UUID;

@FeignClient(name = "voter-service", path = "/internal/voters")
public interface VoterServiceClient {

    @GetMapping("/{id}")
    VoterLookupResponse getVoter(@PathVariable("id") UUID voterId);
}
