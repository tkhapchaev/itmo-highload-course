package ru.tkhapchaev.electionservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tkhapchaev.electionservice.dto.internal.ElectionStatusLookupResponse;
import ru.tkhapchaev.electionservice.entity.Election;
import ru.tkhapchaev.electionservice.service.ElectionService;

import java.util.UUID;

@RestController
@RequestMapping("/internal/elections")
@RequiredArgsConstructor
public class ElectionInternalController {

    private final ElectionService electionService;

    @GetMapping("/{id}/status")
    public ElectionStatusLookupResponse getStatus(@PathVariable UUID id) {
        Election election = electionService.getById(id);
        return new ElectionStatusLookupResponse(
                election.getStatus().getId(),
                election.getStatusCode().name()
        );
    }
}
