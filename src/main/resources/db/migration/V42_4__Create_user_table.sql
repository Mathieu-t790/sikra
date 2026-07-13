create table if not exists "user"
(
    id         uuid         not null,
    first_name varchar(200),
    last_name  varchar(200) not null,
    user_name  varchar(50)  not null,
    email      varchar(255) not null,
    constraint pk_user primary key (id)
);

alter table "user"
    add constraint uc_user_user_name unique (user_name);
alter table "user"
    add constraint uc_user_email unique (email);
