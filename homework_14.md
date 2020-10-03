## HW14: Внедрение docker и consul

**Цель:** В результате выполнения ДЗ вы интегрируете в ваш проект социальной сети docker и auto discovery сервисов с помощью consul

В данном задании тренируются навыки:
- использование docker;
- использование consul;
- построение auto discovery;

**Задачи:**
1) Обернуть сервис диалогов в docker
2) Развернуть consul в вашей системе
3) Интегрировать auto discovery в систему диалогов
4) Научить монолитное приложение находить и нагружать поднятые узлы сервиса диалогов
5) Опционально можно использовать nomad

### Развертывание

Используется docker-compose.yml:
```yaml
# Use root/example as user/password credentials
version: '3.4'

services:
  consul:
    image: consul:1.1.0
    hostname: localhost
    networks:
      - lan
    ports:
      - 8500:8500

  dialogs_service:
    image: avpgenium/dialogs-service:latest
    container_name: "dialogs-service"
    restart: on-failure
    environment:
      CONSUL_HOST: consul
      CONSUL_PORT: 8500
    ports:
      - 9090:9090
    networks:
      - lan

  ...

```

После старта consul доступен на http://localhost:8500.

Микросервис диалогов необходимо зарегистрировать в consul с помощью POST-запроса (либо, что
предпочтительнее, использовать настройку instance-id для автоматической регистрации 
микросервиса при старте):
```shell script
curl -s -XPUT -d"{
  \"Name\": \"dialogs-service\",
  \"ID\": \"dialogs-service\",
  \"Tags\": [ \"chats\", \"messages\" ],
  \"Address\": \"localhost\",
  \"Port\": 9090,
  \"Check\": {
    \"Name\": \"dialogs-service HTTP on port 9090\",
    \"ID\": \"dialogs-service\",
    \"Interval\": \"10s\",
    \"HTTP\": \"dialogs-service:8080/actuator/health\",
    \"Timeout\": \"1s\",
    \"Status\": \"passing\"
  }
}" localhost:8500/v1/agent/service/register
```

Для того, чтобы consul мог проверять работоспособность сервиса диалогов, была добавлена
зависимость на spring-actuator, который предоставляет endpoint для health-check-а.

```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-config</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
        </dependency>
</dependencies>
```

Настройки монолитного бекенда и микросервиса диалогов для работы с consul:
```yaml
spring:
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        # указать instance-id, чтобы сервис сам при запуске зарегистрировался в consul
        instance-id: ${spring.application.name}:${random.value}
        enabled: true
        health-check-interval: 10s
```

Получение информации о сервисе, к которому хотим отправить запрос происходит примено так:

```java
    /**
     * Находит первый из доступных инстансов сервиса по его имени и возвращет его адрес
     * @param serviceName имя сервиса
     * @return URI сервиса для доступа к нему по http
     */
    @Override
    public Optional<URI> serviceUrl(String serviceName) {
        return discoveryClient.getInstances(serviceName)
                .stream()
                .map(ServiceInstance::getUri)
                .findFirst();
    }
```

И затем может использоваться для отправки запросов к сервисам. Например:
```java
    private final RestTemplate restTemplate = new RestTemplate();
    private final DiscoveryService discoveryService;

    /**
     * Отправить новое сообщение в чат
     * @param messageDto новое сообщение в чат
     * @throws ServiceUnavailableException микросервис не доступен
     */
    public String createMessage(MessageDto messageDto) throws ServiceUnavailableException
    {
        URI service = discoveryService.serviceUrl(Constants.ServicesNames.DIALOGS_SERVICE)
                .map(s -> s.resolve(Constants.API_ENDPOINT + "/messages"))
                .orElseThrow(ServiceUnavailableException::new);
        RequestEntity<MessageDto> newMessageDto = new RequestEntity<>(messageDto, HttpMethod.POST, service);
        return restTemplate.postForEntity(service, newMessageDto, String.class)
                .getBody();
    }
```

Скрипты для сборки докер-образов бекенда социальной сети и микросервиса диалогов:
  - [Dockerfile](../social-chat/Dockerfile) сервиса диалогов (готовый образ [dialogs-service](https://hub.docker.com/repository/docker/avpgenium/dialogs-service) на docker-hub).
  - [Dockerfile](../Dockerfile) основного бэкэнда (готовый образ [social-backend](https://hub.docker.com/repository/docker/avpgenium/social-backend) на docker-hub).
  

