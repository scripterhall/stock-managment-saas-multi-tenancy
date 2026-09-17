package com.nourallah.saasapp.config;

/**
 *  TenantContext - stock l'identifiant du tenant courant dans une thread local

 *   Chaque requete HTTP est traitee par un thread dedie
 *   Le ThreadLocal garentie que le tenant_id est isole par thread
 *   meme en cas de requete simultanee de tenants differents

 *   Flux :
 *    1. tenantFilter extrait le tenant_id de la requete HTTP
 *    2. tenantFilter apelle le TenantContext.setCurrentTenant (tenant_id)
 *    3. Le code metier (service, repositories ) accede au tenant via TenantContext.getCurrentTenant()
 *    4. TenantFilter appelle TenantContext.clear() apres la reponse (nettoyage)
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * Definit l'identifiant du tenant pour le thread courant .
     */
    public static void setCurrentTenant(final String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    /**
     * Recupere l'identifiant de tenant pour le thread current .
     */
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    /**
     * appeler dans le bloc finally
     * pour eviter les fuites de memoires (memory leak)
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }

}
