create table if not exists "users_with_roles"
(
    "id"       serial,
    "name"     varchar(255) not null,
    "password" varchar(255) not null,
    "role"     varchar(255) not null,
    constraint "users_with_roles_id_pk" primary key ("id"),
    constraint "role_check" check ( "role" in ('USER', 'ADMIN', 'PERSON'))
);