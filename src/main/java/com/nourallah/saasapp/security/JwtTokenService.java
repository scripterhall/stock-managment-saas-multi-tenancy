package com.nourallah.saasapp.security;


import com.nourallah.saasapp.exceptions.UnauthorizedException;
import com.nourallah.saasapp.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenService {

    private final JwtProperties jwtProperties;
    private  PrivateKey privateKey;
    private  PublicKey publicKey;

    @PostConstruct
    public void init() {

        try{
            this.privateKey = loadPrivateKey(this.jwtProperties.getPrivateKeyPath());
            this.publicKey  = loadPublicKey(this.jwtProperties.getPublicKeyPath());

            log.info("Private key and Public key loaded successfully");

        } catch (Exception e) {
            log.error("Error loading private keys from file . {}", e.getMessage());
            throw new RuntimeException("Error loading private keys from file . " + e.getMessage());
        }

    }


    public String generateAccessToken(
            @NotNull
            final String tenantId ,
            @NotNull
            final String userId,
            final String role){
        final Date now = new Date();
        final Date expiration = new Date(System.currentTimeMillis() + this.jwtProperties.getAccessTokenExpiration());
        return Jwts.builder()
                .subject(userId)
                .claim("tenant_id",tenantId)
                .claim("role",role)
                .issuedAt(now)
                .expiration(expiration)
                .issuer("saas-app")
                .signWith(this.privateKey,Jwts.SIG.RS256)
                .compact();
    }

    public String getUserIdFromToken(String token){
        final Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public String getTenantIdFromToken(String token){
        final Claims claims = getClaimsFromToken(token);
        return claims.get("tenant_id").toString();
    }


    public String getRoleFromToken(String token){
        final Claims claims = getClaimsFromToken(token);
        return claims.get("role").toString();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(this.publicKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (final ExpiredJwtException e){
            throw new UnauthorizedException("Token has expired");
        }catch (final UnsupportedJwtException e){
            throw new UnauthorizedException("Token is not signed or  unsupported");
        }catch (final MalformedJwtException e){
            throw new UnauthorizedException("Token is malformed");
        }catch (final SecurityException e){
            throw new UnauthorizedException("Invalid JWT signature");
        }catch (final IllegalArgumentException e){
            throw new UnauthorizedException("JWT claims string is empty");
        }
    }




    private Claims getClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(this.publicKey).build().parseSignedClaims(token).getPayload();
    }


    private PrivateKey loadPrivateKey(String privateKeyPath) throws Exception {
        try(final InputStream in  = JwtTokenService.class.getClassLoader().getResourceAsStream(privateKeyPath)) {
            if(in == null) {
                throw new RuntimeException("Private key not found");
            }
            final String key = new String(in.readAllBytes());
            final String privateKeyPem = key.replace("-----BEGIN PRIVATE KEY-----","")
                                            .replace("-----END PRIVATE KEY-----","")
                                            .replaceAll("\\s","");
            final byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem); // decoder le cle
            final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
    }

    private PublicKey loadPublicKey(String publicKeyPath) throws Exception {
        try(final InputStream in  = JwtTokenService.class.getClassLoader().getResourceAsStream(publicKeyPath)) {
            if(in == null) {
                throw new RuntimeException("Public key not found");
            }
            final String key = new String(in.readAllBytes());
            final String publicKeyPem = key.replace("-----BEGIN PUBLIC KEY-----","")
                    .replace("-----END PUBLIC KEY-----","")
                    .replaceAll("\\s","");
            final byte[] keyBytes = Base64.getDecoder().decode(publicKeyPem); // decoder le cle
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        }
    }


}
