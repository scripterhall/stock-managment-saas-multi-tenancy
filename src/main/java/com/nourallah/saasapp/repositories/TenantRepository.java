package com.nourallah.saasapp.repositories;

import com.nourallah.saasapp.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, String> {

    boolean existsByCompanyCode(final String companyCode);
    boolean existsByEmail(final String email);
}
