create table vote (
    id uuid primary key,
    candidate_id uuid not null,
    voter_id uuid not null,
    election_id uuid not null,
    user_id uuid not null,
    created_at timestamp with time zone not null,
    constraint uk_vote_voter unique (voter_id),
    constraint uk_vote_user_election unique (user_id, election_id)
);
