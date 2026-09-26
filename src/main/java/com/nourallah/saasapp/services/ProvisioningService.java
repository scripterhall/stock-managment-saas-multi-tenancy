package com.nourallah.saasapp.services;

import com.nourallah.saasapp.entities.Tenant;

public interface ProvisioningService {

    void provisionTenant(final Tenant tenant);
}
