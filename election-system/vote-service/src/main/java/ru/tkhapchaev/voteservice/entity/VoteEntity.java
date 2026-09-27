package ru.tkhapchaev.voteservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "vote",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vote_voter", columnNames = "voter_id"),
                @UniqueConstraint(name = "uk_vote_user_election", columnNames = {"user_id", "election_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class VoteEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    @Column(name = "voter_id", nullable = false)
    private UUID voterId;

    @Column(name = "election_id", nullable = false)
    private UUID electionId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
