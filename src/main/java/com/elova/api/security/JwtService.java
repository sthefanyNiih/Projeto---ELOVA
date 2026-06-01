package com.elova.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Serviço responsável por gerar, validar e extrair informações
 * dos tokens JWT usados na autenticação.
 * Módulo 1 - Auth / Security.
 */
@Service
public class JwtService {

    @Value("${elova.jwt.secret}")
    private String secret;

    @Value("${elova.jwt.expiration}")
    private long expiration;

    @Value("${elova.jwt.reset-expiration}")
    private long resetExpiration;

    // ==========================================
    // GERAÇÃO
    // ==========================================

    /**
     * Gera um JWT para o usuário autenticado.
     * O subject é o e-mail do usuário.
     */
    public String gerarToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Gera um token de curta duração para recuperação de senha.
     * Inclui a claim "tipo" = "reset" para distinguir do token de auth.
     */
    public String gerarTokenReset(String email) {
        return Jwts.builder()
                .subject(email)
                .claim("tipo", "reset")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + resetExpiration))
                .signWith(getSecretKey())
                .compact();
    }

    // ==========================================
    // EXTRAÇÃO
    // ==========================================

    /** Extrai o e-mail (subject) do token. */
    public String extrairEmail(String token) {
        return parsearClaims(token).getSubject();
    }

    /** Verifica se o token é do tipo reset de senha. */
    public boolean isTokenReset(String token) {
        Claims claims = parsearClaims(token);
        return "reset".equals(claims.get("tipo", String.class));
    }

    // ==========================================
    // VALIDAÇÃO
    // ==========================================

    /**
     * Valida o token: verifica assinatura, expiração e se
     * o subject corresponde ao e-mail esperado.
     */
    public boolean isTokenValido(String token, String email) {
        try {
            String emailToken = extrairEmail(token);
            return emailToken.equals(email) && !isTokenExpirado(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpirado(String token) {
        return parsearClaims(token).getExpiration().before(new Date());
    }

    // ==========================================
    // INTERNO
    // ==========================================

    private Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        // A chave precisa ter ao menos 256 bits; usamos base64 para garantir
        byte[] keyBytes = secret.getBytes();
        // Se o secret for menor que 32 bytes, padeia (só para dev - configure corretamente em prod)
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            return Keys.hmacShaKeyFor(padded);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
