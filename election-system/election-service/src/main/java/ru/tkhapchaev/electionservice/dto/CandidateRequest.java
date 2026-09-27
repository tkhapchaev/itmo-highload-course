package ru.tkhapchaev.electionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CandidateRequest(
        @NotBlank String name,
        String description,
        @NotNull UUID electionId
) {
}
