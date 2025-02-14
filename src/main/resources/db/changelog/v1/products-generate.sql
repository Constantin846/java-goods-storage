--liquibase formatted sql
--changeset Author:1
insert into products(id, name, article, description, category, price, count, last_count_update_time, create_date)
select
    gen_random_uuid(),
    md5(random()::text),
    gen_random_uuid(),
    md5(random()::text),
    md5(random()::text),
    100,
    10,
    '2025-02-12 18:17:01',
    '2025-02-12'
from generate_series(1, 1000000);
