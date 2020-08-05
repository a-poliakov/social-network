# Отчет "Сравнительное тестирование ClickHouse и MySQL"

**Цель:** В результате выполнения ДЗ вы построите аналитический отчет в MySQL и ClickHouse, а также сравните их производительность. В данном задании тренируются навыки: - работа с ClickHouse; - выбор СУБД в зависимости от задачи;

1) Сделать копию таблицы пользователей из MySQL в Clickhouse.
2) Сделать отчет по распределению пользователей по возрасту и полу на MySQL и Clickhouse.
3) Сравнить время построения отчета. Объяснить результат.

**Требования:**

- Описан процесс переноса данных из MySQL в Clickhouse.
- Описан запрос получения аналитики, а также индексы в обеих СУБД.
- Индексы должны быть корректными.
- Тестирование должно быть корректно.
- Полученный результат времени выполнения должен быть адекватен.

## Подготовка

## Настройка MySQL

Для таблицы ``social_network.users`` был создан индекс:
```mysql
CREATE INDEX age_and_sex_idx ON social_network.users(age, sex);
```

## Настройка ClickHouse и перенос данных
Для переноса данных использовалась программа [clickhouse-mysql-data-reader](https://github.com/Altinity/clickhouse-mysql-data-reader/blob/master/docs/manual.md).
Подробная инструкция приведена в мануале в репозитории github.

Была создана таблица в clickhouse:
```shell script
clickhouse-client -mn < create_clickhouse_table_template.sql
```

Созданная таблица (скрипт из [create_clickhouse_table_template.sql](create_clickhouse_table_template.sql)):
```clickhouse
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
```
Составной индекс по полям age и sex в движке MergeTree такой же как в MySQL. 
Для гранулированности индекса оставлено значение по умолчанию равное 8192.

Затем были перенесены данные из mysql в clickhouse:
```shell script
clickhouse-mysql \
    --src-host=127.0.0.1 \
    --src-user=root \
    --src-password=example \
    --migrate-table \
    --src-tables=social_network.users \
    --dst-host=127.0.0.1
```

Перенос данных занимает значительное время (у меня перенос 100 млн записей занял около двух дней).

## Тестирование и анализ результатов

Тестовые наборы данных были сгенерированны с помощью утилиты [data-generator](/data-generator).

Сначала были сгенерированы 1 млн записей - тут результаты clickhouse и mysql очень близки, 
mysql даже быстрее немножко (возможно это из-за того, что в mysql какие-то данные из таблицы помещаются в оперативную память).

Затем были сгенерированы наборы из 10 млн и 100 млн записей и ClickHouse строит отчет быстрее MySQL.
(теоретически при еще большем объеме данных разница будет еще лучше заметна, но тут уже физические ограничения моей машины).

Результаты сравнения для запроса `SELECT age, sex, count(*) FROM users GROUP BY age, sex ORDER BY age` приведены ниже:

Кол-во записей | Mysql (с) | ClickHouse (с)  
--- | --- | --- 
1 млн | 1.023  | 1.387 
10 млн | 181.380  | 2.562 
100 млн | 739.530 | 9.613

## Полезные ссылки
1. [Семейство движков MergeTree](https://clickhouse.tech/docs/ru/engines/table-engines/mergetree-family/mergetree/)
2. [clickhouse docker compose](https://github.com/rongfengliang/clickhouse-docker-compose)
