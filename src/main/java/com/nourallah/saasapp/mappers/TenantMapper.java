package com.nourallah.saasapp.mappers;


import com.nourallah.saasapp.entities.Tenant;
import com.nourallah.saasapp.requests.RegisterTenantRequest;
import com.nourallah.saasapp.responses.TenantResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TenantMapper {

    public Tenant toEntity(final RegisterTenantRequest request) {
        return Tenant.builder()
                .companyName(request.getCompanyName())
                .companyCode(request.getCompanyCode())
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .adminFullName(request.getAdminFullName())
                .adminEmail(request.getAdminEmail())
                .adminUsername(request.getAdminUsername())
                .adminPassword(request.getAdminPassword())
                .build();
    }

    public TenantResponse toResponse(Tenant tenant) {
        return TenantResponse.builder()
                .companyName(tenant.getCompanyName())
                .companyCode(tenant.getCompanyCode())
                .email(tenant.getEmail())
                .adminFullName(tenant.getAdminFullName())
                .adminEmail(tenant.getAdminEmail())
                .adminUsername(tenant.getAdminUsername())
                .build();
    }
}
