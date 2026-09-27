package ru.tkhapchaev.voterservice.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VoterRequest(
        @NotNull UUID userId,
        @NotNull UUID electionId
) {
}
