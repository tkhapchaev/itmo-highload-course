create table vote_audit (
    id uuid primary key,
    vote_id uuid not null unique,
    election_id uuid not null,
    candidate_id uuid not null,
    voter_id uuid not null,
    occurred_at timestamp with time zone not null,
    received_at timestamp with time zone not null
);
