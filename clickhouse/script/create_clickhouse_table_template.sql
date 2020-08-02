CREATE TABLE default.user (
    id Int64,
    login String,
    password String,
    first_name String,
    last_name String,
    age UInt8,
    sex UInt8,
    interests String,
    city String,
    date Date DEFAULT toDate(time),
    time DateTime DEFAULT now()
)
    ENGINE = MergeTree()
    PARTITION BY (age,sex)
ORDER BY id;