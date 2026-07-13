alter table file_submission
    add column user_id uuid,
    add constraint fk_file_submission_on_user foreign key (user_id) references "user" (id);

alter table file_submission
    drop column email;
