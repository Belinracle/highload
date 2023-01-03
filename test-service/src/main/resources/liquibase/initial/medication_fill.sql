--liquibase formatted sql

--changeset ddzhukashev:1
-- password is password
insert into medication (cost, description, medname)
 values (10,'Medication description 1','Medication 1');