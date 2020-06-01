package ru.apolyakov.social_network.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for replicated data sources
 * <code>@Primary</code> and <code>@DependsOn</code> are the key requirements for Spring Boot.
 *
 * @author apolyakov
 * @since 31.05.2020
 */
@Configuration
public class JpaConfig {
    /**
     * Main DataSource
     * <p>
     * Application must use this dataSource.
     */
    @Primary
    @Bean
    // @DependsOn required!! thanks to Michel Decima
    @DependsOn({"writeDataSource", "readDataSource", "routingDataSource"})
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    /**
     * AbstractRoutingDataSource and it's sub classes must be initialized as Spring Bean for calling
     * {@link AbstractRoutingDataSource#afterPropertiesSet()}.
     */
    @Bean("routingDataSource")
    public DataSource routingDataSource(@Qualifier("writeDataSource") DataSource writeDataSource,
                                        @Qualifier("readDataSource") DataSource readDataSource) {
        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("write", writeDataSource);
        dataSourceMap.put("read", readDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);

        return routingDataSource;
    }

    @Bean("writeDataSource")
    public DataSource writeDataSource(@Value("${replication.master.url}") String host,
                                      @Value("${replication.master.username}") String username,
                                      @Value("${replication.master.password}") String password) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:mysql://" + host + "/social_network");
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }

    @Bean("readDataSource")
    public DataSource readDataSource(@Value("${replication.slave.url}") String host,
                                     @Value("${replication.slave.username}") String username,
                                     @Value("${replication.slave.password}") String password) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:mysql://" + host + "/social_network");
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }
}
