begin transaction;

-- Table to store the providers and how to make a request to them.
create table if not exists provider
(
    id           int generated always as identity primary key,
    name         varchar              not null,
    url          varchar              not null,
    frequency    interval             not null,
    is_active    boolean default true not null,
    last_fetched timestamp with time zone
);

-- Table to store the fetched data.
create table if not exists raw_data
(
    provider_id int,
    fetch_time  timestamp with time zone not null,
    data        jsonb                    not null,

    primary key (provider_id, fetch_time),
    foreign key (provider_id) references provider (id)
);

commit;
