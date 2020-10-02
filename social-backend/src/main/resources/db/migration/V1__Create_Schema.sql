# create schema if not exists ru.apolyakov.social_network;

create table if not exists users
(
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    login       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    first_name  VARCHAR(100) NOT NULL,
    second_name VARCHAR(100) NOT NULL,
    sex         VARCHAR(10),
    age         INT,
    interests   VARCHAR(1024),
    city        VARCHAR(100)
);

create table if not exists user_subscription
(
    user_id   BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id)
);