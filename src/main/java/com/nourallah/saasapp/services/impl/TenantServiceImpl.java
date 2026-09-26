package com.nourallah.saasapp.services.impl;

import com.nourallah.saasapp.common.PageResponse;
import com.nourallah.saasapp.entities.Tenant;
import com.nourallah.saasapp.entities.TenantStatus;
import com.nourallah.saasapp.entities.User;
import com.nourallah.saasapp.entities.UserRole;
import com.nourallah.saasapp.exceptions.DuplicateResourceException;
import com.nourallah.saasapp.exceptions.InvalidRequestException;
import com.nourallah.saasapp.mappers.TenantMapper;
import com.nourallah.saasapp.repositories.TenantRepository;
import com.nourallah.saasapp.repositories.UserRepository;
import com.nourallah.saasapp.requests.RegisterTenantRequest;
import com.nourallah.saasapp.responses.TenantResponse;
import com.nourallah.saasapp.services.ProvisioningService;
import com.nourallah.saasapp.services.TenantService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {
    private final TenantRepository tenantRepository;
    private final TenantMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final ProvisioningService  provisioningService;

    @Transactional
    @Override
    public void registerTenant(RegisterTenantRequest request) {

        // check if the tenant already exists by company code
        if(tenantRepository.existsByCompanyCode(request.getCompanyCode())) {
            throw new DuplicateResourceException("Tenant already exists");
        }
        //check if email already exists
        if(tenantRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Tenant Email already exists");
        }

        //create tenant entity
        final Tenant tenant = this.mapper.toEntity(request);
        tenant.setAdminPassword(passwordEncoder.encode(request.getAdminPassword()));

        tenant.setStatus(TenantStatus.PENDING);
        tenantRepository.save(tenant);
    }

    @Override
    public void approveTenant(String tenantId) {

        //check if tenant exists
        final Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));
        //activate tenant
        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);
        try{
        // provision the schema for the tenant (creer les schema et les tables)
            this.provisioningService.provisionTenant(tenant);
        // create initial admin user  ( dans le catch si quelque chose ne vas pas en doit supprimer la schema qu'on a fait )
        createInitialAdminUser(tenant);
        }catch (Exception e){
            this.rollBackTenantStatus(tenant);
        }

    }


    @Override
    public void activateTenant(String tenantId) {
        final Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));

        if(tenant.getStatus() != TenantStatus.PENDING) {
            throw new InvalidRequestException("Tenant is not pending");
        }

        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);
    }

    @Override
    public void deactivateTenant(String tenantId) {

        final Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));

        if(tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Tenant is not pending");
        }

        tenant.setStatus(TenantStatus.INACTIVE);
        tenantRepository.save(tenant);

    }

    @Override
    public void suspendTenant(String tenantId) {
        final Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));

        if(tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Tenant is not pending");
        }

        tenant.setStatus(TenantStatus.SUSPENDED);
        tenantRepository.save(tenant);
    }

    @Override
    public PageResponse<TenantResponse> findAll(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Tenant> tenants = tenantRepository.findAll(pageRequest);
        final Page<TenantResponse> tenantResponses = tenants.map(mapper::toResponse);
        return PageResponse.of(tenantResponses);
    }

    private void createInitialAdminUser(Tenant tenant) {
        //check if user already exists
        if(this.userRepository.existsByUsername(tenant.getAdminUsername())){
            throw new DuplicateResourceException("Admin Username already exists");
        }
        final User adminUser = User.builder()
                .username(tenant.getAdminUsername())
                .email(tenant.getAdminEmail())
                .firstName(extractFirstName(tenant.getAdminFullName(), 0))
                .lastName(extractFirstName(tenant.getAdminFullName(), 1))
                .password(tenant.getAdminPassword())
                .role(UserRole.ROLE_COMPANY_ADMIN)
                .tenant(tenant)
                .enabled(true)
                .build();
        this.userRepository.save(adminUser);


    }

    private String extractFirstName(final String fullName,int index){
        if(index == 1){
            return fullName.split(" ").length > 1 ? fullName.split(" ")[1] : fullName;
        }
        return fullName.split(" ")[index];
    }


    private void rollBackTenantStatus(final Tenant tenant){
        tenant.setStatus(TenantStatus.PENDING);
        this.tenantRepository.save(tenant);
    }
}
