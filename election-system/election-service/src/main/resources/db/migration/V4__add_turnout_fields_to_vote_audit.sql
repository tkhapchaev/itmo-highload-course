alter table vote_audit
    add column if not exists turnout_after_vote double precision not null default 0,
    add column if not exists quorum_reached_after_vote boolean not null default false;
