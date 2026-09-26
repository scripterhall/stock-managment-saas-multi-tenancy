package com.nourallah.saasapp.controllers;


import com.nourallah.saasapp.requests.LoginRequest;
import com.nourallah.saasapp.requests.RegisterTenantRequest;
import com.nourallah.saasapp.responses.LoginResponse;
import com.nourallah.saasapp.services.AuthenticationService;
import com.nourallah.saasapp.services.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication" , description = "Authentication API")
public class AuthenticationController {

    private final AuthenticationService authService;
    private final TenantService tenantService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid
            @RequestBody
            final RegisterTenantRequest request
            ){
        this.tenantService.registerTenant(request);
        return ResponseEntity.ok().build();
    }
}
