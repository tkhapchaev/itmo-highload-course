package ru.tkhapchaev.electionservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tkhapchaev.electionservice.dto.internal.CandidateLookupResponse;
import ru.tkhapchaev.electionservice.entity.Candidate;
import ru.tkhapchaev.electionservice.service.CandidateService;

import java.util.UUID;

@RestController
@RequestMapping("/internal/candidates")
@RequiredArgsConstructor
public class CandidateInternalController {

    private final CandidateService candidateService;

    @GetMapping("/{id}")
    public CandidateLookupResponse getCandidate(@PathVariable UUID id) {
        Candidate candidate = candidateService.getById(id);
        return new CandidateLookupResponse(candidate.getId(), candidate.getElection().getId());
    }
}
