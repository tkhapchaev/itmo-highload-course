package ru.tkhapchaev.voterservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank String name,
        String description
) {
}
