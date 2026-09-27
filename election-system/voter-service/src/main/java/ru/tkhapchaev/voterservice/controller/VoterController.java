package ru.tkhapchaev.voterservice.controller;

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
import ru.tkhapchaev.voterservice.dto.VoterRequest;
import ru.tkhapchaev.voterservice.dto.VoterResponse;
import ru.tkhapchaev.voterservice.entity.VoterEntity;
import ru.tkhapchaev.voterservice.service.VoterService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/voters")
@RequiredArgsConstructor
public class VoterController {

    private final VoterService voterService;

    @GetMapping
    public CollectionModel<EntityModel<VoterResponse>> getAll(@RequestParam(required = false) UUID electionId) {
        List<EntityModel<VoterResponse>> models = voterService.getAll(electionId).stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(models,
                relative(linkTo(methodOn(VoterController.class).getAll(electionId)).withSelfRel()));
    }

    @GetMapping("/{id}")
    public EntityModel<VoterResponse> getById(@PathVariable UUID id) {
        return toModel(voterService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<VoterResponse>> create(@Valid @RequestBody VoterRequest request) {
        VoterEntity voter = new VoterEntity();
        voter.setUserId(request.userId());
        voter.setElectionId(request.electionId());

        VoterEntity created = voterService.create(voter);
        return ResponseEntity.created(URI.create("/api/voters/" + created.getId()))
                .body(toModel(created));
    }

    @PutMapping("/{id}")
    public EntityModel<VoterResponse> update(@PathVariable UUID id, @Valid @RequestBody VoterRequest request) {
        VoterEntity voter = new VoterEntity();
        voter.setUserId(request.userId());
        voter.setElectionId(request.electionId());

        return toModel(voterService.update(id, voter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        voterService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<VoterResponse> toModel(VoterEntity voter) {
        UUID id = voter.getId();
        VoterResponse response = new VoterResponse(
                id,
                voter.getUserId(),
                voter.getElectionId(),
                voter.getCreatedAt()
        );

        return EntityModel.of(
                response,
                relative(linkTo(methodOn(VoterController.class).getById(id)).withSelfRel()),
                relative(linkTo(methodOn(VoterController.class).getAll(voter.getElectionId())).withRel("voters"))
        );
    }

    private static Link relative(Link link) {
        return link.withHref(link.getHref().replaceFirst("^https?://[^/]+", ""));
    }
}
