package ru.tkhapchaev.electionservice.dto;

import java.time.Instant;
import java.util.UUID;

public record CandidateResponse(
        UUID id,
        String name,
        String description,
        UUID electionId,
        Instant createdAt,
        Instant updatedAt
) {
}
