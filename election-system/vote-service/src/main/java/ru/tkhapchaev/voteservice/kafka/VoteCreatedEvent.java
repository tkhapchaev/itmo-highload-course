package ru.tkhapchaev.voteservice.kafka;

import java.time.Instant;
import java.util.UUID;

public record VoteCreatedEvent(
        UUID voteId,
        UUID candidateId,
        UUID voterId,
        UUID electionId,
        UUID userId,
        Instant createdAt
) {
}
