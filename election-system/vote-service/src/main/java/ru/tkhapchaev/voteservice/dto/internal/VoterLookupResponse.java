package ru.tkhapchaev.voteservice.dto.internal;

import java.util.UUID;

public record VoterLookupResponse(UUID voterId, UUID userId, UUID electionId) {
}
