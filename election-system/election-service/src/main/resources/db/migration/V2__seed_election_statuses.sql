insert into election_status (id, name) values
    (0, 'OPEN'),
    (1, 'ACTIVE'),
    (2, 'CLOSED'),
    (3, 'CANCELED')
on conflict (id) do nothing;
