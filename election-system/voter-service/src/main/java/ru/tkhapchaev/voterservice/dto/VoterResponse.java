package ru.tkhapchaev.voterservice.dto;

import java.time.Instant;
import java.util.UUID;

public record VoterResponse(
        UUID id,
        UUID userId,
        UUID electionId,
        Instant createdAt
) {
}
