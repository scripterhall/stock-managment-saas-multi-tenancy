package com.nourallah.saasapp.services.impl;

import com.nourallah.saasapp.common.PageResponse;
import com.nourallah.saasapp.config.TenantContext;
import com.nourallah.saasapp.entities.User;
import com.nourallah.saasapp.entities.UserRole;
import com.nourallah.saasapp.exceptions.DuplicateResourceException;
import com.nourallah.saasapp.exceptions.InvalidRequestException;
import com.nourallah.saasapp.mappers.UserMapper;
import com.nourallah.saasapp.repositories.TenantRepository;
import com.nourallah.saasapp.repositories.UserRepository;
import com.nourallah.saasapp.requests.UserRequest;
import com.nourallah.saasapp.responses.UserResponse;
import com.nourallah.saasapp.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final TenantRepository tenantRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        return this.repository.findByUsername(username)
                .orElseThrow( () -> new UsernameNotFoundException("No user was found with : "+username));
    }

    @Override
    public void createUser(UserRequest request) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Creating user with id {}, tenantId {}", request.getUsername(), tenantId);

        // validate if user exists by username
        if(this.repository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // vérifier si email exists
        if(this.repository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email already exists");
        }

        // validate role (cannot be PLATFORM admin)
        if(request.getRole() == UserRole.ROLE_PLATFORM_ADMIN){
            throw new InvalidRequestException("Role required");
        }

        final User user = this.userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.repository.save(user);
        log.info("User with id {} has been created", user.getUsername());

    }

    @Override
    public void updateUser(String userId, UserRequest request) {
        final String tenanntId = TenantContext.getCurrentTenant();
        log.info("Updating user with id {}, tenantId {}", userId, tenanntId);
        final User user = this.repository.findByIdAndNotDeleted(userId).orElseThrow(
                () -> new EntityNotFoundException("User does not exist with id : " + userId)
        );
        // check if user belongs to the tenant
        if(!user.getTenant().getId().equals(tenanntId)){
            throw new InvalidRequestException("User does not belong to this Tenant");
        }

        // check if username is being changed and if it is already taken
        if(!user.getUsername().equals(request.getUsername()) && this.repository.existsByUsername(request.getUsername()) ) {
            throw new DuplicateResourceException("Username already exists");
        }

        // check if email is being changed and if it is already taken
        if(user.getEmail().equals(request.getEmail()) && this.repository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email already exists");
        }

        // validate role (cannot be PLATFORM admin)
        if(request.getRole() == UserRole.ROLE_PLATFORM_ADMIN){
            throw new InvalidRequestException("Role required");
        }

        // update user details
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        this.repository.save(user);

        log.info("User with id {} has been updated", user.getUsername());
    }

    @Override
    public void deleteUser(String userId) {
        final String tenanntId = TenantContext.getCurrentTenant();
        log.info("Deleting user with id {}, tenantId {}", userId, tenanntId);
        final User user = this.repository.findByIdAndNotDeleted(userId).orElseThrow(
                () -> new EntityNotFoundException("User does not exist with id : " + userId)
        );

        // check if user belongs to the tenant
        if(!user.getTenant().getId().equals(tenanntId)){
            throw new InvalidRequestException("User does not belong to this Tenant");
        }

        // soft delete user
        user.setDeleted(true);
        this.repository.save(user);



    }

    @Override
    public UserResponse getUserById(String userId) {
       final User user =  this.repository.findByIdAndNotDeleted(userId).orElseThrow(
               () -> new EntityNotFoundException("User does not exist with id : " + userId)
       );

       // check if user belong to the current tenant
        if(!user.getTenant().getId().equals(TenantContext.getCurrentTenant())){
            throw new InvalidRequestException("User does not belong to this Tenant");
        }

        return this.userMapper.toResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getUsers(int page, int size) {
        final String tenantId = TenantContext.getCurrentTenant();
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<User> userPage = this.repository.findAllByTenantId(tenantId,pageRequest);
        final Page<UserResponse> userResponses = userPage.map(userMapper::toResponse);
        return PageResponse.of(userResponses);
    }

    @Override
    public void enableUser(String userId) {
        final String tenanntId = TenantContext.getCurrentTenant();
        final User user = this.repository.findByIdAndNotDeleted(userId).orElseThrow(
                () -> new EntityNotFoundException("User does not exist with id : " + userId)
        );
        // check if user belongs to the tenant
        if(!user.getTenant().getId().equals(tenanntId)){
            throw new InvalidRequestException("User does not belong to this Tenant");
        }

        user.setEnabled(true);
        this.repository.save(user);
        log.info("User with id {} has been enabled", user.getUsername());

    }

    @Override
    public void disableUser(String userId) {

        final String tenanntId = TenantContext.getCurrentTenant();
        final User user = this.repository.findByIdAndNotDeleted(userId).orElseThrow(
                () -> new EntityNotFoundException("User does not exist with id : " + userId)
        );
        // check if user belongs to the tenant
        if(!user.getTenant().getId().equals(tenanntId)){
            throw new InvalidRequestException("User does not belong to this Tenant");
        }
        user.setEnabled(false);
        this.repository.save(user);
        log.info("User with id {} has been disabled", user.getUsername());

    }
}
