--liquibase formatted sql
--changeset Author:2-2.1
CREATE INDEX customer_login_hash_index ON customer USING hash(login);
