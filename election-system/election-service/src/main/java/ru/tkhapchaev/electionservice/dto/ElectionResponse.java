package ru.tkhapchaev.electionservice.dto;

import java.time.Instant;
import java.util.UUID;

public record ElectionResponse(
        UUID id,
        String name,
        String description,
        String status,
        int statusId,
        double quorum,
        Instant createdAt,
        Instant updatedAt
) {
}
