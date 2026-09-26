package com.nourallah.saasapp.services.impl;

import com.nourallah.saasapp.entities.User;
import com.nourallah.saasapp.requests.LoginRequest;
import com.nourallah.saasapp.responses.LoginResponse;
import com.nourallah.saasapp.security.JwtTokenService;
import com.nourallah.saasapp.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;



    @Override
    public LoginResponse login(LoginRequest request) {
        final Authentication authentication  = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        final User  user = (User) authentication.getPrincipal();




        final String tenantId = user.getTenantId() != null ? user.getTenantId() : "";
        final String token = this.jwtTokenService.generateAccessToken(tenantId, user.getId(), user.getRole().name());
        final String tokenType = "Bearer";
        return LoginResponse.builder()
                .access_token(token).tokenType(tokenType).build();
    }
}
