package com.nourallah.saasapp.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantHibernateFilter {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.nourallah.saasapp.services.*.*(..))")
    public void activateTenantFilter(){
        final String tenantId = TenantContext.getCurrentTenant(); // le tenant en cours
        if(tenantId != null){
            final Session session = entityManager.unwrap(Session.class);

            // active le filtre et inject le parametre tenantID
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        }

    }
}
