package com.nourallah.saasapp.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * TenantFIlter intercept chaque requete http pour identifier le tenant

 *  Ce filtre est le point d'entrer de mecanisme de multi-tenant

 *  il s'execute avant toutes les controlleur

 *  Strategie d'identification du tenant (par order de priorite):
 *  1. Header "X-TENANT-ID"
 *  2.(Opptionnel) Sous-demain: alpha.stockapp.com : -> "alpha"

 *   Si aucun tenant n'est identifier -> reponse 400 BAD request
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter implements Filter {


    private static final String TENANT_HEADER =  "X-TENANT-ID";

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse resp = (HttpServletResponse) response;

        final String tenantId = resolveTenant(req);
        if (tenantId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write(
                    "{\"error\":\"Tenant ID is missing in the request header. add X-TENANT-ID\"}"
            );
            return;
        }
        try{
            //stocker le tenant id dans le thread local
            TenantContext.setCurrentTenant(tenantId);
            chain.doFilter(request, response);

        } finally {
            // CRITIQUE : clean the thread for no memory leaks (for the second request)
            TenantContext.clear();

        }

    }

    private String resolveTenant(HttpServletRequest req) {
        final String tenantId = req.getHeader(TENANT_HEADER);
        if (tenantId != null && !tenantId.isBlank()) {
            return tenantId.trim().toLowerCase();
        }
        return null;
    }
}
