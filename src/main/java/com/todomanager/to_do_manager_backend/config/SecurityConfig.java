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
        // Configure Spring Security
        http
            .csrf(csrf -> csrf.disable()) // Updated to use the Lambda DSL
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/tasks/**").authenticated() // Protect task-related endpoints
                .anyRequest().permitAll() // Allow other endpoints
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())) // Updated to use the Lambda DSL
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Replace with your Okta JWKS URI
        return NimbusJwtDecoder.withJwkSetUri("https://dev-65717442.okta.com/oauth2/default/v1/keys").build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        // Optionally customize JWT conversion if needed
        return converter;
    }
}