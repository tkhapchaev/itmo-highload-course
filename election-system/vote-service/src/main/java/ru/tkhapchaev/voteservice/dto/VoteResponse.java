package ru.tkhapchaev.voteservice.dto;

import java.time.Instant;
import java.util.UUID;

public record VoteResponse(
        UUID id,
        UUID candidateId,
        UUID voterId,
        UUID electionId,
        UUID userId,
        Instant createdAt
) {
}
