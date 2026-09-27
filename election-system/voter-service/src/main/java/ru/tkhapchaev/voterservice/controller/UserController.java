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
import org.springframework.web.bind.annotation.RestController;
import ru.tkhapchaev.voterservice.dto.UserRequest;
import ru.tkhapchaev.voterservice.dto.UserResponse;
import ru.tkhapchaev.voterservice.entity.UserEntity;
import ru.tkhapchaev.voterservice.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public CollectionModel<EntityModel<UserResponse>> getAll() {
        List<EntityModel<UserResponse>> models = userService.getAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(models,
                relative(linkTo(methodOn(UserController.class).getAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public EntityModel<UserResponse> getById(@PathVariable UUID id) {
        return toModel(userService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> create(@Valid @RequestBody UserRequest request) {
        UserEntity user = new UserEntity();
        user.setName(request.name());
        user.setDescription(request.description());

        UserEntity created = userService.create(user);

        return ResponseEntity.created(URI.create("/api/users/" + created.getId()))
                .body(toModel(created));
    }

    @PutMapping("/{id}")
    public EntityModel<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
        UserEntity user = new UserEntity();
        user.setName(request.name());
        user.setDescription(request.description());

        return toModel(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<UserResponse> toModel(UserEntity user) {
        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getDescription(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

        return EntityModel.of(
                response,
                relative(linkTo(methodOn(UserController.class).getById(user.getId())).withSelfRel()),
                relative(linkTo(methodOn(UserController.class).getAll()).withRel("users"))
        );
    }

    private static Link relative(Link link) {
        return link.withHref(link.getHref().replaceFirst("^https?://[^/]+", ""));
    }
}
