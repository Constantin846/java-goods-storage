--liquibase formatted sql
--changeset Author:5-1.1
CREATE INDEX order_business_key_hash_index ON order_app USING hash(business_key);
