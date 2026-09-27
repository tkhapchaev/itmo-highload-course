package ru.tkhapchaev.electionservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tkhapchaev.electionservice.dto.CandidateRequest;
import ru.tkhapchaev.electionservice.dto.CandidateResponse;
import ru.tkhapchaev.electionservice.entity.Candidate;
import ru.tkhapchaev.electionservice.entity.Election;
import ru.tkhapchaev.electionservice.service.CandidateService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    public CollectionModel<EntityModel<CandidateResponse>> getAll(@RequestParam(required = false) UUID electionId) {
        List<EntityModel<CandidateResponse>> models = candidateService.getAll(electionId).stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(models,
                relative(linkTo(methodOn(CandidateController.class).getAll(electionId)).withSelfRel()));
    }

    @GetMapping("/{id}")
    public EntityModel<CandidateResponse> getById(@PathVariable UUID id) {
        return toModel(candidateService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<CandidateResponse>> create(@Valid @RequestBody CandidateRequest request) {
        Candidate candidate = new Candidate();
        candidate.setName(request.name());
        candidate.setDescription(request.description());

        Election election = new Election();
        election.setId(request.electionId());
        candidate.setElection(election);

        Candidate created = candidateService.create(candidate);
        EntityModel<CandidateResponse> model = toModel(created);

        return ResponseEntity
                .created(URI.create("/api/candidates/" + created.getId()))
                .body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<CandidateResponse> update(@PathVariable UUID id, @Valid @RequestBody CandidateRequest request) {
        Candidate candidate = new Candidate();
        candidate.setName(request.name());
        candidate.setDescription(request.description());

        Election election = new Election();
        election.setId(request.electionId());
        candidate.setElection(election);

        return toModel(candidateService.update(id, candidate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        candidateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<CandidateResponse> toModel(Candidate candidate) {
        UUID id = candidate.getId();
        UUID electionId = candidate.getElection().getId();

        CandidateResponse response = new CandidateResponse(
                candidate.getId(),
                candidate.getName(),
                candidate.getDescription(),
                electionId,
                candidate.getCreatedAt(),
                candidate.getUpdatedAt()
        );

        return EntityModel.of(
                response,
                relative(linkTo(methodOn(CandidateController.class).getById(id)).withSelfRel()),
                relative(linkTo(methodOn(CandidateController.class).getAll(electionId)).withRel("candidates")),
                relative(linkTo(methodOn(ElectionController.class).getById(electionId)).withRel("election"))
        );
    }

    private static Link relative(Link link) {
        return link.withHref(link.getHref().replaceFirst("^https?://[^/]+", ""));
    }
}
