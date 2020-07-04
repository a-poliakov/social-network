# Отчет "Репликация из MySQL в tarantool"

**Цель:** В результате выполнения ДЗ вы настроите репликацию из MySQL в tarantool, а также напишите запрос на lua. 
В данном задании тренируются навыки: 
- администрирование MySQL; 
- администрирование tarantool; 
- разработка хранимых процедур для tarantool.

**Задание:**          
1) Выбрать любую таблицу, которую мы читаем с реплик MySQL.
2) С помощью программы https://github.com/tarantool/mysql-tarantool-replication настроить реплицирование в tarantool (лучше всего версии 1.10).
3) Выбрать любой запрос и переписать его на lua-процедуру на tarantool.
4) Провести нагрузочное тестирование, сравнить tarantool и MySQL по производительности.

**Требования:**

- Репликация из MySQL в tarantool работает.
- Хранимые процедуры в tarantool написаны корректно.
- Хранимые процедуры выполнены по code style на примере репозитория Mail.Ru.
- Нагрузочное тестирование проведено.

## Подготовка
Была выбрана таблица users и запрос поиска пользователей по частичному совпадению имени/фамилии.
Для tarantool были написана lua-процедуры для поиска пользователей по префиксу имени/фамилии. 
Также был создан составной индекс по first_name и second_name для поиска пользователей по нему.

Для подключения к tarantool бекенда социальной сети и работы с ним использовался
[Java connector for Tarantool 1.7.4+](https://github.com/tarantool/tarantool-java).


Tarantool запускается с помощью официального докер-образа (https://github.com/tarantool/docker). 
Кластер mysql с настроенной репликацией взят из предыдущего домашнего задания (репликация master-slave с одним slave-ом).

MySQL slave replication daemon for Tarantool (https://github.com/tarantool/mysql-tarantool-replication) к сожалению не имеет docker образа. 
Устанавливать его нативно (на windows) также не удобно, так как для этого нужен linux. 

**Решение**: собрать свой докер образ с ним (докер-образ размещен на dockerhub: https://hub.docker.com/r/avpgenium/mysql-tarantool-replication). 
При сборке образа использовалась инструкция из официального репозитория репликатора.
Был настроен файл конфигурации replicatord.yml (вместо replicatord.conf).
Предполагается, что mysql, tarantool и replicator будут запускаться в одной docker-сети,
поэтому вместо ip-адресов в конфиге прописаны алиасы (mysql_master и tarantool), соответствующие именам контейнеров. 

```dockerfile
FROM centos:7

# install git, make and dependencies
RUN yum update -y && \
    yum -y install ncurses-devel git make cmake gcc gcc-c++ boost boost-devel mysql-devel mysql-lib

# clone replicator git repository
RUN git clone https://github.com/tarantool/mysql-tarantool-replication.git

# update git submodules and build replicator
RUN cd mysql-tarantool-replication && \
    git submodule update --init --recursive  && \
    cmake . && \
    make && \
    cp replicatord /usr/local/sbin/replicatord

# copy replicator config files
COPY ./replicatord.service /etc/systemd/system
COPY ./replicatord.yml /usr/local/etc/replicatord.yml
COPY ./init.sh /

CMD ["/init.sh"]
```

Итоговый docker-compose файл приведен ниже:

```yaml
version: '3'
services:
  mysql_master:
    image: mysql:5.7
    container_name: "mysql_master"
    env_file:
      - ./master/mysql_master.env
    restart: "no"
    ports:
      - 3306:3306
    volumes:
      - ./master/conf/mysql.conf.cnf:/etc/mysql/conf.d/mysql.conf.cnf
      - ./master/data:/var/lib/mysql
    networks:
      - test_tarantul

  tarantool:
    image: tarantool/tarantool:1.10.2
    container_name: "tarantool"
    networks:
      - test_tarantul
    ports:
      - "3301:3301"
    volumes:
      - ./tarantool/conf/init.lua:/opt/tarantool/init.lua
      - ./tarantool/conf/config.yml:/etc/tarantool/config.yml
      - ./tarantool/data:/var/lib/tarantool

  replicator:
    image: avpgenium/mysql-tarantool-replication:latest
    container_name: "mysql-tarantool-replication"
    networks:
      - test_tarantul

networks:
  test_tarantul:
```

Краткая инструкция по запуску:
- Запустить контейнер с mysql master-узлом.
- На mysql master-узле создать пользователя для репликации:
```sql
CREATE USER <username>@'<host>' IDENTIFIED BY '<password>';

GRANT REPLICATION CLIENT ON *.* TO 'mydb_slave_user'@'%' IDENTIFIED BY 'mydb_slave_pwd';
GRANT REPLICATION SLAVE ON *.* TO 'mydb_slave_user'@'%' IDENTIFIED BY 'mydb_slave_pwd';
GRANT SELECT ON *.* TO 'mydb_slave_user'@'%' IDENTIFIED BY 'mydb_slave_pwd';

FLUSH PRIVILEGES
```
- запускаем tarantool 
- запускаем replicator

## Нагрузочное тестирование

Тестирование проводилось по одному и тому же api сервиса для двух конфигураций репликации:
- mysql master - mysql slave
- mysql master - replicator - tarantool slave

База данных наполнена 1 млн записей пользователей, сгенерированных с помощью утилиты [data-generator](/data-generator).

Для подачи нагрузки использовался скрипт [jmeter](/replication/Load_read_TestCase.jmx).

Тип метрики | Mysql replication | Tarantool replication  
--- | --- | --- 
latency (ms), 100 connections | 943 | 410  
throughput, 100 connections | 81,0 |  234,9

По результатам нагрузочного тестирования:
- latency у tarantool меньше примерно в 1,5-2 раза
- throughput у tarantool больше примерно в 2,5-3 раза