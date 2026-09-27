create table users (
    id uuid primary key,
    name varchar(255) not null,
    description text,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table voter (
    id uuid primary key,
    user_id uuid not null,
    election_id uuid not null,
    created_at timestamp with time zone not null,
    constraint uk_voter_user_election unique (user_id, election_id)
);
