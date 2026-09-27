package ru.tkhapchaev.voteservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tkhapchaev.voteservice.dto.CountResponse;
import ru.tkhapchaev.voteservice.service.VoteService;

import java.util.UUID;

@RestController
@RequestMapping("/internal/votes")
@RequiredArgsConstructor
public class VoteInternalController {

    private final VoteService voteService;

    @GetMapping("/count")
    public CountResponse countByElectionId(@RequestParam UUID electionId) {
        return new CountResponse(voteService.countByElectionId(electionId));
    }
}
