package ru.apolyakov.social_network.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.tarantool.*;
import ru.shadam.tarantool.core.SimpleSocketChannelProvider;
import ru.shadam.tarantool.core.TarantoolKeyValueTemplate;
import ru.shadam.tarantool.repository.configuration.EnableTarantoolRepositories;

import java.util.List;

/**
 * https://github.com/tarantool/tarantool-java
 */
@Configuration
//@EnableTarantoolRepositories
public class TarantoolDataSourceConfig {
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
}
