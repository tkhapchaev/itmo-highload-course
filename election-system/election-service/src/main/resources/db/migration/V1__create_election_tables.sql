create table election_status (
    id integer primary key,
    name varchar(255) not null
);

create table election (
    id uuid primary key,
    name varchar(255) not null,
    description text,
    status_id integer not null,
    quorum double precision not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    constraint fk_election_status
        foreign key (status_id) references election_status (id)
);

create table candidate (
    id uuid primary key,
    name varchar(255) not null,
    description text,
    election_id uuid not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    constraint fk_candidate_election
        foreign key (election_id) references election (id)
);
