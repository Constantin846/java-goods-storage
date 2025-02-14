--liquibase formatted sql
--changeset Author:1
CREATE ALIAS  h2RandomUUID AS '
    UUID h2RandomUUIDMy() {
        return UUID.randomUUID();
    }
';