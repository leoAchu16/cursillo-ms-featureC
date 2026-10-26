package org.cursillo.facturacion.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Documentación pública: el gateway la lee sin token
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // Consultar facturas y detalles: secretaría y alumno
                        .requestMatchers(HttpMethod.GET, "/facturas/**", "/detalles-facturas/**")
                        .hasAnyAuthority("SECRETARIA", "ALUMNO")
                        // Todo lo demás de facturación y conceptos: solo secretaría
                        .requestMatchers("/facturas/**", "/detalles-facturas/**", "/conceptos/**")
                        .hasAuthority("SECRETARIA")
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            log.warn("401 - acceso sin autenticación válida a {} {}", req.getMethod(), req.getRequestURI());
                            escribir(res, 401, "Unauthorized", "Token ausente, inválido o expirado");
                        })
                        .accessDeniedHandler((req, res, ex) -> {
                            log.warn("403 - acceso denegado a {} {}", req.getMethod(), req.getRequestURI());
                            escribir(res, 403, "Forbidden", "No tenés permisos para realizar esta acción");
                        }))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static void escribir(HttpServletResponse res, int status, String error, String message)
            throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
                status, error, message, LocalDateTime.now()));
    }
}