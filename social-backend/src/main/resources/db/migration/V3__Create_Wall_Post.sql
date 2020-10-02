create table if not exists wall_post
(
    id              BIGINT          NOT NULL,
    from_user_id    BIGINT          NOT NULL,
    to_user_id      BIGINT          NOT NULL,
    post_body       varchar(5000)   NOT NULL,
    date_created    timestamp       NOT NULL,
    PRIMARY KEY (id)
);

create index wall_post_from_user_idx on wall_post(from_user_id);
create index wall_post_to_user_idx on wall_post(to_user_id);