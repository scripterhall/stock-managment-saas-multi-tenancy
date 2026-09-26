package com.nourallah.saasapp.services;

import com.nourallah.saasapp.common.PageResponse;
import com.nourallah.saasapp.requests.RegisterTenantRequest;
import com.nourallah.saasapp.responses.TenantResponse;

public interface TenantService {

    void registerTenant(final RegisterTenantRequest request);
    void approveTenant(final String tenantId);
    void activateTenant(final String tenantId);
    void deactivateTenant(final String tenantId);
    void suspendTenant(final String tenantId);
    PageResponse<TenantResponse> findAll(final int page , final int size);
}
