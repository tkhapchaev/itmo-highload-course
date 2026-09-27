package ru.tkhapchaev.electionservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tkhapchaev.electionservice.dto.ElectionStatusResponse;
import ru.tkhapchaev.electionservice.entity.ElectionStatus;
import ru.tkhapchaev.electionservice.service.ElectionStatusService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/election-statuses")
@RequiredArgsConstructor
public class ElectionStatusController {

    private final ElectionStatusService electionStatusService;

    @GetMapping
    public CollectionModel<EntityModel<ElectionStatusResponse>> getAll() {
        List<EntityModel<ElectionStatusResponse>> models = electionStatusService.getAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(models, relative(linkTo(ElectionStatusController.class).withSelfRel()));
    }

    @GetMapping("/{id}")
    public EntityModel<ElectionStatusResponse> getById(@PathVariable Integer id) {
        return toModel(electionStatusService.getById(id));
    }

    private EntityModel<ElectionStatusResponse> toModel(ElectionStatus status) {
        ElectionStatusResponse response = new ElectionStatusResponse(status.getId(), status.getName());

        return EntityModel.of(
                response,
                relative(linkTo(methodOn(ElectionStatusController.class).getById(status.getId())).withSelfRel()),
                relative(linkTo(ElectionStatusController.class).withRel("election-statuses"))
        );
    }

    private static Link relative(Link link) {
        return link.withHref(link.getHref().replaceFirst("^https?://[^/]+", ""));
    }
}
