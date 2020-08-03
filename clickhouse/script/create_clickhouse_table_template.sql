CREATE TABLE social_network.users (
    id Int64,
    login String,
    password String,
    first_name String,
    second_name String,
    age UInt8,
    sex UInt8,
    interests String,
    city String,
    date Date DEFAULT toDate(time),
    time DateTime DEFAULT now()
)
    ENGINE = MergeTree(date, (age, sex), 8192)
    PARTITION BY (age,sex)
ORDER BY id;