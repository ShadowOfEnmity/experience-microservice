-- auto-generated definition
create table industries
(
    id  bigint auto_increment
        primary key,
    name varchar(30) not null,
    constraint name
        unique (name),
    message_id UUID not null unique
);

