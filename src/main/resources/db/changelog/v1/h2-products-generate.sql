--liquibase formatted sql
--changeset Author:3
insert into product(id, name, article, description, category, price, count, last_count_update_time, create_date)
select
    h2RandomUUID(),
    'name',
    h2RandomUUID(),
    'des',
    'UNDEFINED',
    100,
    100,
    '2025-02-12 18:17:01',
    '2025-02-12'
from
    generate_series(1, 200000);
-- todo set 1M