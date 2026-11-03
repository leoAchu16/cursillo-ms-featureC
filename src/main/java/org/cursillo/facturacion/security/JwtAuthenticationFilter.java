package org.cursillo.facturacion.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.issuer:cursillo-ms-auth}")
    private String jwtIssuer;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Misma decodificación que cursillo-ms-auth (Base64 estándar)
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(jwtIssuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            List<?> rawRoles = claims.get("roles", List.class);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (rawRoles != null) {
                for (Object r : rawRoles) {
                    String role = String.valueOf(r);
                    authorities.add(new SimpleGrantedAuthority(role));
                    if (!role.startsWith("ROLE_")) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                }
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.info("JWT autenticado en facturación: usuario='{}', autoridades={}", username, authorities);

        } catch (Exception e) {
            log.warn("Token JWT inválido en facturación: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}