package ru.apolyakov.social_network.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisProperties {
    private String hostname;
    private int port;
    private int maxActive;
    private int maxIdle;
    private int minIdle;
    private int ttlSeconds;
}
