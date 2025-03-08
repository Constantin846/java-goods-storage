--liquibase formatted sql
--changeset Author:3-1.1
CREATE INDEX order_product_order_id_hash_index ON order_product USING hash(order_id);
CREATE INDEX order_product_product_id_hash_index ON order_product USING hash(product_id);
