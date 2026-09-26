package com.nourallah.saasapp.services.impl;

import com.nourallah.saasapp.entities.Tenant;
import com.nourallah.saasapp.exceptions.TenantProvioningException;
import com.nourallah.saasapp.services.ProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvisioningServiceImpl implements ProvisioningService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;


    @Override
    public void provisionTenant(Tenant tenant) {
        final String schemaName = "tenant_" +  tenant.getCompanyCode().toLowerCase();
        try{
            log.info("Provisioning tenant: {} (schema: {})", tenant.getCompanyName(), schemaName);
            // 1. create the Postgres schema
            createSchema(schemaName);
            log.info("Schema created successfully: {}", schemaName);

            // 2. Run Flyway migrations for this schema
            runTenantMigrations(schemaName);
            log.info("tenant migrations completed successfully for schema: {}", schemaName);

            // 3. Initialize the default data (Optional)
            initializeDefaultData(schemaName , tenant);

        } catch (final Exception e) {
            log.error("Provisioning tenant failed for schema: {}", schemaName, e);

            // rollback: drop schema creation
            try{
                dropSchema(schemaName);

            }catch (Exception e1){
                log.error("Failed to rollback schema creation for tenant: {}",tenant.getCompanyName(), e1);
            }
            throw new TenantProvioningException("Failed to provision tenant");
        }
    }

    private void dropSchema(String schemaName) {
        this.jdbcTemplate.execute(String.format("DROP SCHEMA IF EXISTS %s CASCADE", schemaName));
    }


    private void createSchema(String schemaName) {
        final String sql = String.format("CREATE SCHEMA IF NOT EXISTS %s;", schemaName);
        jdbcTemplate.execute(sql);

    }


    private void runTenantMigrations(String schemaName) {

        log.info("Running tenant migrations for schema: {}", schemaName);
        final Flyway tenantFlyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .locations("classpath:db/migration/tenant")
                .baselineOnMigrate(true)
                .table("flyway_schema_history")
                .validateOnMigrate(true)
                .cleanDisabled(true)
                .load();
        log.info("Tenant migrations started: {}", schemaName);
        tenantFlyway.migrate();
        log.info("Tenant migrations completed: {}", schemaName);
    }


    private void initializeDefaultData(String schemaName, Tenant tenant) {
        log.info("Initializing default data for tenant: {}", tenant.getCompanyName());
        // here you can  add default data initialization code
    }
}
