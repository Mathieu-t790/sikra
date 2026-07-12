create table if not exists "user"
(
    id         uuid         not null,
    first_name varchar(200),
    last_name  varchar(200) not null,
    user_name  varchar(50)  not null,
    email      varchar(255) not null,
    constraint pk_user primary key (id)
);

create table if not exists student
(
    id        uuid        not null,
    user_id   uuid,
    reference varchar(50) not null,
    level     varchar(10),
    constraint pk_student primary key (id)
);

create table if not exists course
(
    id    uuid         not null,
    title varchar(255) not null,
    start timestamp without time zone,
    "end" timestamp without time zone,
    constraint pk_course primary key (id)
);

create table if not exists subscription
(
    id         uuid         not null,
    created_at timestamp without time zone,
    status     varchar(50),
    course_id  uuid,
    user_id    uuid,
    constraint pk_subscription primary key (id)
);

create table if not exists email_history
(
    id               uuid         not null,
    subscription_id  uuid,
    recipient        varchar(255),
    subject          varchar(255),
    status           varchar(50),
    error_message    text,
    sent_at          timestamp without time zone,
    constraint pk_email_history primary key (id)
);

alter table "user"
    add constraint uc_user_user_name unique (user_name);
alter table "user"
    add constraint uc_user_email unique (email);

alter table student
    add constraint uc_student_reference unique (reference);

alter table course
    add constraint uc_course_title unique (title);

alter table student
    add constraint fk_student_on_user foreign key (user_id) references "user" (id);

alter table subscription
    add constraint fk_subscription_on_course foreign key (course_id) references course (id);
alter table subscription
    add constraint fk_subscription_on_user foreign key (user_id) references "user" (id);

alter table email_history
    add constraint fk_email_history_on_subscription foreign key (subscription_id) references subscription (id);
