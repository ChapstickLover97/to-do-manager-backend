package com.todomanager.to_do_manager_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity (not recommended for production)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/tasks/**").authenticated() // Protect task-related endpoints
                .anyRequest().permitAll() // Allow all other endpoints without authentication
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())) // Use JWT for OIDC
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Ensure the correct JWK URI is configured for your Okta Authorization Server
        return NimbusJwtDecoder.withJwkSetUri("https://dev-65717442.okta.com/oauth2/default/v1/keys").build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        // Customize roles mapping if necessary (e.g., extracting roles from JWT claims)
        return converter;
    }
}