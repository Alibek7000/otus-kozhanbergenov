DROP table if exists users;
create table users
(
    id       bigserial primary key,
    login    varchar(255),
    password varchar(255),
    nickname varchar(255)
);

DROP table if exists accounts;
create table accounts
(
    id     bigserial primary key,
    amount bigint,
    tp     varchar(255),
    status varchar(255)
);