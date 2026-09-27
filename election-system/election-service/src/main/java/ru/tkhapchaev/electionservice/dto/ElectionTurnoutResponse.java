package ru.tkhapchaev.electionservice.dto;

import java.util.UUID;

public record ElectionTurnoutResponse(
        UUID electionId,
        long voters,
        long votes,
        double turnout,
        double quorum,
        boolean quorumReached
) {
}
