package ru.tkhapchaev.voterservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tkhapchaev.voterservice.dto.CountResponse;
import ru.tkhapchaev.voterservice.dto.internal.VoterLookupResponse;
import ru.tkhapchaev.voterservice.entity.VoterEntity;
import ru.tkhapchaev.voterservice.service.VoterService;

import java.util.UUID;

@RestController
@RequestMapping("/internal/voters")
@RequiredArgsConstructor
public class VoterInternalController {

    private final VoterService voterService;

    @GetMapping("/{id}")
    public VoterLookupResponse getById(@PathVariable UUID id) {
        VoterEntity voter = voterService.getById(id);
        return new VoterLookupResponse(voter.getId(), voter.getUserId(), voter.getElectionId());
    }

    @GetMapping("/count")
    public CountResponse countByElectionId(@RequestParam UUID electionId) {
        return new CountResponse(voterService.countByElectionId(electionId));
    }
}
