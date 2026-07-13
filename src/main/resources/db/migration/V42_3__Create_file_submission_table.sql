create table if not exists file_submission
(
    id         uuid        not null,
    file_key   varchar(500) not null,
    file_name  varchar(500) not null,
    email      varchar(255) not null,
    created_at timestamp   not null default now(),
    constraint file_submission_pk primary key (id)
);
