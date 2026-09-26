package com.nourallah.saasapp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.MultiTenancySettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> , HibernatePropertiesCustomizer {

    private final DataSource dataSource;


    @Override
    public Connection getAnyConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        log.debug("Getting connection for tenant {}", tenantIdentifier);
        final Connection connection = getAnyConnection();
        try{
            if(tenantIdentifier != null && !tenantIdentifier.equals("public") && !tenantIdentifier.isBlank()){
                connection.createStatement().execute("SET search_path  TO " +  tenantIdentifier + " , public");
                log.trace("Set search_path to {}",tenantIdentifier);
            }
        } catch (final SQLException e) {
            log.error(" Error getting connection for tenant: {}",tenantIdentifier, e);
            throw e;
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try{
            if(tenantIdentifier != null && !tenantIdentifier.equals("public")){
                connection.createStatement().execute("SET search_path  TO  public");
            }
        } catch (final SQLException e) {
            log.error(" Error getting connection for tenant: {}",tenantIdentifier, e);
            throw e;
        }
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(@NonNull Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(@NonNull Class<T> unwrapType) {
        return null;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(MultiTenancySettings.MULTI_TENANT_CONNECTION_PROVIDER,this);
    }
}
