package com.nourallah.saasapp.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantSchemaResolver {
    private final JdbcTemplate jdbcTemplate;
    private static final String PUBLIC_SCHEMA = "PUBLIC";

    @Cacheable(value = "tenantSchemas" , key = "#tenantId")
    public String resolveTenantSchema(String tenantId) {
        if(tenantId == null || tenantId.isBlank()) {
            return PUBLIC_SCHEMA;
        }
        try{
            final String companyCode = this.jdbcTemplate
                    .queryForObject("""
                     SELECT company_code 
                     FROM public.tenants 
                     WHERE id = ? 
                     AND deleted = false
                    """, String.class, tenantId);
            System.out.println("here is companyCode : "+companyCode);
            if(companyCode != null) {
                final String schemaName = "tenant_"+companyCode.toLowerCase();
                log.debug("Resolved tenant schema: {} for tenant: {}", schemaName, tenantId);
                return schemaName;
            }
            log.warn("Unable to find tenant schema for tenant: {} , using public schema", tenantId);
            return PUBLIC_SCHEMA;
        } catch (Exception e){
            log.error("Unable to resolve tenant schema for tenant: {} ", tenantId, e);
            return PUBLIC_SCHEMA;
        }
    }
}
