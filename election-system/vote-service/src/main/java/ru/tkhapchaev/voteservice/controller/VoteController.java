package ru.tkhapchaev.voteservice.controller;

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
import ru.tkhapchaev.voteservice.dto.VoteRequest;
import ru.tkhapchaev.voteservice.dto.VoteResponse;
import ru.tkhapchaev.voteservice.entity.VoteEntity;
import ru.tkhapchaev.voteservice.service.VoteService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @GetMapping
    public CollectionModel<EntityModel<VoteResponse>> getAll(
            @RequestParam(required = false) UUID candidateId,
            @RequestParam(required = false) UUID userId
    ) {
        List<EntityModel<VoteResponse>> models = voteService.getAll(candidateId, userId).stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(models,
                relative(linkTo(methodOn(VoteController.class).getAll(candidateId, userId)).withSelfRel()));
    }

    @GetMapping("/{id}")
    public EntityModel<VoteResponse> getById(@PathVariable UUID id) {
        return toModel(voteService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<VoteResponse>> create(@Valid @RequestBody VoteRequest request) {
        VoteEntity vote = new VoteEntity();
        vote.setCandidateId(request.candidateId());
        vote.setVoterId(request.voterId());

        VoteEntity created = voteService.create(vote);

        return ResponseEntity.created(URI.create("/api/votes/" + created.getId()))
                .body(toModel(created));
    }

    @PutMapping("/{id}")
    public EntityModel<VoteResponse> update(@PathVariable UUID id, @Valid @RequestBody VoteRequest request) {
        VoteEntity vote = new VoteEntity();
        vote.setCandidateId(request.candidateId());
        vote.setVoterId(request.voterId());

        return toModel(voteService.update(id, vote));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        voteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<VoteResponse> toModel(VoteEntity vote) {
        UUID id = vote.getId();
        VoteResponse response = new VoteResponse(
                vote.getId(),
                vote.getCandidateId(),
                vote.getVoterId(),
                vote.getElectionId(),
                vote.getUserId(),
                vote.getCreatedAt()
        );

        return EntityModel.of(
                response,
                relative(linkTo(methodOn(VoteController.class).getById(id)).withSelfRel()),
                relative(linkTo(methodOn(VoteController.class).getAll(vote.getCandidateId(), vote.getUserId())).withRel("votes"))
        );
    }

    private static Link relative(Link link) {
        return link.withHref(link.getHref().replaceFirst("^https?://[^/]+", ""));
    }
}
