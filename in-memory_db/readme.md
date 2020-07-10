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
Была выбрана таблица users и запрос поиска пользователей по частичному совпадению имени И фамилии.

### Подготовка БД
Для tarantool были написана lua-процедуры для поиска пользователей по префиксу имени/фамилии. 
Также был создан составной индекс по first_name и second_name для поиска пользователей по нему.

```lua
function start()
    -- таблица "пользователи"
    box.schema.space.create('users', { if_not_exists = true })

    -- индексы для поиска по таблице
    box.space.users:create_index('primary', { type = "TREE", unique = true, parts = { 1, 'unsigned' }, if_not_exists = true })
    box.space.users:create_index('first_second_name_idx', { type = 'TREE', unique = false, parts = {4, 'string', 5, 'string' }, if_not_exists = true })
    box.space.users:create_index('first_name_idx', { type = 'TREE', unique = false, parts = { 4, 'string' }, if_not_exists = true })
    box.space.users:create_index('second_name_idx', { type = 'TREE', unique = false, parts = { 5, 'string' }, if_not_exists = true })

    -- тестовые данные для отладки запроса
    box.space.users:insert({1, "admin1", "123", "aaa", "bbb"})
    box.space.users:insert({2, "admin1", "123", "aaa", "bba"})
    box.space.users:insert({3, "admin1", "123", "aaa", "bbc"})
    box.space.users:insert({4, "admin1", "123", "aaa", "bbd"})
    box.space.users:insert({5, "admin1", "123", "aaa", "bdd"})
    box.space.users:insert({6, "admin1", "123", "aaa", "ddd"})
    box.space.users:insert({7, "admin1", "123", "aaa", "eee"})
    box.space.users:insert({8, "admin1", "123", "aaa", "aba"})
    box.space.users:insert({9, "admin1", "123", "aaa", "aaa"})
    box.space.users:insert({10, "admin1", "123", "aaa", "aab"})
    box.space.users:insert({11, "admin1", "123", "baa", "bbb"})
    box.space.users:insert({12, "admin1", "123", "baaaa", "bba"})
    box.space.users:insert({13, "admin1", "123", "baa", "bbc"})
    box.space.users:insert({14, "admin1", "123", "baa", "bbd"})
    box.space.users:insert({15, "admin1", "123", "bbb", "bdd"})
    box.space.users:insert({16, "admin1", "123", "bbb", "ddd"})
    box.space.users:insert({17, "admin1", "123", "abb", "eee"})
    box.space.users:insert({18, "admin1", "123", "bbb", "aba"})
    box.space.users:insert({19, "admin1", "123", "bbb", "aaa"})
    box.space.users:insert({20, "admin1", "123", "bbb", "aab"})
end

-- procedure for search by first name prefix AND second name prefix
-- Param: prefix_first_name - prefix for searching first name by like '%first_name'
-- Param: prefix_second_name - prefix for searching second name by like '%second_name'
-- Param: size - max count of entries in response
function search_by_first_second_name(prefix_first_name, prefix_second_name, size)
    local count = 0
    local result = {}
    for _, tuple in box.space.users.index.first_second_name_idx:pairs(prefix_first_name, { iterator = 'GE' }) do
        if string.startswith(tuple[4], prefix_first_name, 1, -1) and string.startswith(tuple[5], prefix_second_name, 1, -1) then
            table.insert(result, tuple)
            count = count + 1
            if count >= size then
                return result
            end
        end
    end
    return result
end

-- procedure for search by first name prefix
-- Param: prefix - prefix for searching first name by like '%first_name'
function search_by_first_name(prefix)
    local result = {}
    for _, tuple in box.space.users.index.first_name_idx:pairs({ prefix }, { iterator = 'GE' }) do
        if string.startswith(tuple[4], prefix, 1, -1) then
            table.insert(result, tuple)
        end
    end
    return result
end

-- procedure for search by second name prefix
-- Param: prefix - prefix for searching first name by like '%second_name'
function search_by_second_name(prefix)
    local result = {}
    for _, tuple in box.space.users.index.second_name_idx:pairs({ prefix }, { iterator = 'GE' }) do
        if string.startswith(tuple[5], prefix, 1, -1) then
            table.insert(result, tuple)
        end
    end
    return result
end
```

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

### Подготовка бекенда

Для подключения к tarantool бекенда социальной сети и работы с ним использовался
[Java connector for Tarantool 1.7.4+](https://github.com/tarantool/tarantool-java).

Необходимо добавить зависимости в pom.xml:
```xml
<dependencies>
    <!-- TARANTOOL -->
	<dependency>
		<groupId>ru.shadam</groupId>
		<artifactId>spring-data-tarantool</artifactId>
		<version>0.1.0</version>
	</dependency>
	<dependency>
		<groupId>org.tarantool</groupId>
		<artifactId>connector</artifactId>
		<version>1.9.4</version>
	</dependency>
</dependencies>
```

Для настройки источника данных и работы spring-data-tarantool необходимо помимо прочего настроить бины:
```java
    @Bean(destroyMethod = "close")
    public TarantoolClient tarantoolClient(TarantoolProperties tarantoolProperties) {
        TarantoolClientConfig config = new TarantoolClientConfig();
        config.username = tarantoolProperties.getUsername();
        config.password = tarantoolProperties.getPassword();

        SimpleSocketChannelProvider channelProvider = new SimpleSocketChannelProvider("localhost", 3301);

        return new TarantoolClientImpl(channelProvider, config);
    }

    @Bean
    public DriverManagerDataSource tarantoolDataSource(TarantoolProperties tarantoolProperties) {
        return new DriverManagerDataSource(tarantoolProperties.getJdbcUrl());
    }

    @Bean
    public TarantoolClientOps<Integer, List<?>, Object, List<?>> tarantoolSyncOps(TarantoolClient tarantoolClient) {
        return tarantoolClient.syncOps();
    }
```


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