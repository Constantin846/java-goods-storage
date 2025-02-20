--liquibase formatted sql
--changeset Author:1
CREATE INDEX product_article_hash_index ON product USING hash(article);