--liquibase formatted sql

--changeset ddzhukashev:1
create table clientFile
(
    id         bigint generated by default as identity
        constraint users_pkey
            primary key,
    userEmail      varchar(255),
    filename        varchar(255)
);

