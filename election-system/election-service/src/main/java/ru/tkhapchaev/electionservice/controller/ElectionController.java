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
import ru.tkhapchaev.electionservice.dto.ElectionRequest;
import ru.tkhapchaev.electionservice.dto.ElectionResponse;
import ru.tkhapchaev.electionservice.dto.ElectionTurnoutResponse;
import ru.tkhapchaev.electionservice.entity.Election;
import ru.tkhapchaev.electionservice.service.ElectionService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    @GetMapping
    public CollectionModel<EntityModel<ElectionResponse>> getAll(@RequestParam(required = false) Integer statusId) {
        List<EntityModel<ElectionResponse>> models = electionService.getAll(statusId).stream()
                .map(this::toModel)
                .toList();

        Link self = statusId == null
                ? relative(linkTo(ElectionController.class).withSelfRel())
                : relative(linkTo(methodOn(ElectionController.class).getAll(statusId)).withSelfRel());

        return CollectionModel.of(models, self);
    }

    @GetMapping("/{id}")
    public EntityModel<ElectionResponse> getById(@PathVariable UUID id) {
        Election election = electionService.getById(id);
        return toModel(election);
    }

    @GetMapping("/{id}/turnout")
    public EntityModel<ElectionTurnoutResponse> getTurnout(@PathVariable UUID id) {
        ElectionTurnoutResponse turnout = electionService.getTurnout(id);
        return EntityModel.of(
                turnout,
                relative(linkTo(methodOn(ElectionController.class).getTurnout(id)).withSelfRel()),
                relative(linkTo(methodOn(ElectionController.class).getById(id)).withRel("election"))
        );
    }

    @PostMapping
    public ResponseEntity<EntityModel<ElectionResponse>> create(@Valid @RequestBody ElectionRequest request) {
        Election election = new Election();
        election.setName(request.name());
        election.setDescription(request.description());
        election.setQuorum(request.quorum());

        Election created = electionService.create(election, request.statusId());
        EntityModel<ElectionResponse> model = toModel(created);

        return ResponseEntity
                .created(URI.create("/api/elections/" + created.getId()))
                .body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<ElectionResponse> update(@PathVariable UUID id, @Valid @RequestBody ElectionRequest request) {
        Election election = new Election();
        election.setName(request.name());
        election.setDescription(request.description());
        election.setQuorum(request.quorum());

        Election updated = electionService.update(id, election, request.statusId());
        return toModel(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        electionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<ElectionResponse> toModel(Election election) {
        UUID id = election.getId();
        ElectionResponse response = new ElectionResponse(
                election.getId(),
                election.getName(),
                election.getDescription(),
                election.getStatus().getName(),
                election.getStatus().getId(),
                election.getQuorum(),
                election.getCreatedAt(),
                election.getUpdatedAt()
        );

        return EntityModel.of(
                response,
                relative(linkTo(methodOn(ElectionController.class).getById(id)).withSelfRel()),
                relative(linkTo(ElectionController.class).withRel("elections")),
                relative(linkTo(methodOn(ElectionController.class).getTurnout(id)).withRel("turnout")),
                relative(linkTo(methodOn(CandidateController.class).getAll(id)).withRel("candidates"))
        );
    }

    private static Link relative(Link link) {
        return link.withHref(link.getHref().replaceFirst("^https?://[^/]+", ""));
    }
}
