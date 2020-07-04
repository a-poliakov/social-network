package ru.apolyakov.social_network.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource-tarantool")
@Data
public class TarantoolProperties {
    private String jdbcUrl;
    private String host;
    private String username;
    private String password;
}
