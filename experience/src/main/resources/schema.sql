create table experience
(
    id              bigint auto_increment primary key,
    description     varchar(255) not null,
    industry_id     bigint       not null
);
