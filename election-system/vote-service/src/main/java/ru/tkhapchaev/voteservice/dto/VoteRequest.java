package ru.tkhapchaev.voteservice.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VoteRequest(
        @NotNull UUID candidateId,
        @NotNull UUID voterId
) {
}
