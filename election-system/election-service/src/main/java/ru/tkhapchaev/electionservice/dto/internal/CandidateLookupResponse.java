package ru.tkhapchaev.electionservice.dto.internal;

import java.util.UUID;

public record CandidateLookupResponse(UUID candidateId, UUID electionId) {
}
