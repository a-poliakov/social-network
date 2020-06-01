package ru.apolyakov.social_network.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * ReplicationRoutingDataSource routes connections by <code>@Transaction(readOnly=true|false)</code>
 */
@Slf4j
public class ReplicationRoutingDataSource  extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceType = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "read" : "write";
        log.info("current dataSourceType : {}", dataSourceType);
        return dataSourceType;
    }
}
