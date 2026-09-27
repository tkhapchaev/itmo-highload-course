package ru.tkhapchaev.electionservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vote_audit")
@Getter
@Setter
@NoArgsConstructor
public class VoteAudit {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID voteId;

    @Column(nullable = false)
    private UUID electionId;

    @Column(nullable = false)
    private UUID candidateId;

    @Column(nullable = false)
    private UUID voterId;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(nullable = false)
    private Double turnoutAfterVote;

    @Column(nullable = false)
    private Boolean quorumReachedAfterVote;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant receivedAt;
}
