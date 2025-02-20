--liquibase formatted sql
--changeset Author:2
CREATE ALIAS  h2RandomUUID AS '
    UUID h2RandomUUIDMy() {
        return UUID.randomUUID();
    }
';