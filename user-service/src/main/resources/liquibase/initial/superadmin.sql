--liquibase formatted sql

--changeset ddzhukashev:1
-- password is password
insert into users (email,first_name,last_name,password,status)
 values ('superadmin_email','admin','super','$2a$12$jugPfqpBDpGcQx5.KSiXau7fFBUDIy3AE1sWyvwP02zyMSjHZfkQC','ACTIVE');
insert into user_roles (user_id, roles) values
 ((SELECT id FROM users WHERE email = 'superadmin_email'),'SUPER_ADMIN')