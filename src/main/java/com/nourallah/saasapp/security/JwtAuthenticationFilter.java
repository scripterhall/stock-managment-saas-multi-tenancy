package com.nourallah.saasapp.security;

import com.nourallah.saasapp.config.TenantContext;
import com.nourallah.saasapp.config.TenantSchemaResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final TenantSchemaResolver tenantSchemaResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().contains("/api/v1/auth/login")){
            filterChain.doFilter(request,response);
        }

        try{
            final String jwt = getJwtFromRequest(request);
            if(StringUtils.hasText(jwt) &&  jwtTokenService.validateToken(jwt)){
                final String userId = jwtTokenService.getUserIdFromToken(jwt);
                final String tenantId = jwtTokenService.getTenantIdFromToken(jwt);
                final String role = jwtTokenService.getRoleFromToken(jwt);
                log.trace(" tenantId: {}", tenantId);
                if(tenantId != null){
                    //stocker le tenant Id et le schemaName
                    TenantContext.setCurrentTenant(tenantId);
                    final String schemaName = this.tenantSchemaResolver.resolveTenantSchema(tenantId);
                    TenantContext.setCurrentSchema(schemaName);
                }

                //create auth token
                final SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.singletonList(authority)
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("User authenticated for user ID: {} , tenant : {} , role: {}", userId, tenantId, role);
            }
        } catch (final Exception e){
            log.error("error authenticating user ", e);
        }
        filterChain.doFilter(request,response);
        TenantContext.clear();

    }

    private String getJwtFromRequest(HttpServletRequest request) {

        final String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
