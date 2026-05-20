package com.projeto.inventario.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${app.jwt.secret:seu_secret_key_muito_seguro_com_minimo_32_caracteres}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24 horas em milissegundos
    private long jwtExpirationMs;

    /**
     * Gera um token JWT baseado no usuário
     * @param userDetails Informações do usuário autenticado
     * @return Token JWT assinado
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Extrai o nome de usuário do token JWT
     * @param token Token JWT
     * @return Nome de usuário extraído do token
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extrai a data de expiração do token
     * @param token Token JWT
     * @return Data de expiração
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extrai um claim específico do token
     * @param token Token JWT
     * @param claimsResolver Função para extrair o claim
     * @return Valor do claim extraído
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Obtém todos os claims do token
     * @param token Token JWT
     * @return Claims contendo informações do token
     */
    private Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica se o token está expirado
     * @param token Token JWT
     * @return true se expirado, false caso contrário
     */
    private Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Valida se o token é autêntico e não está expirado
     * @param token Token JWT
     * @param userDetails Informações do usuário
     * @return true se o token é válido para esse usuário
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida apenas a estrutura e expiração do token (sem validar usuário)
     * @param token Token JWT
     * @return true se o token é válido
     */
    public Boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
