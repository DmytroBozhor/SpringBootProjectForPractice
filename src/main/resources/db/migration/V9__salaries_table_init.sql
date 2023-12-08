create table if not exists "salaries"
(
    "id"       serial,
    "position" varchar(255),
    "employee" int,
    constraint "id_p" primary key ("id"),
    constraint "position_check" check ( "position" in ('developer', 'hr', 'manager')),
    constraint "employee_fk" foreign key ("employee") references "users" ("id")
);

