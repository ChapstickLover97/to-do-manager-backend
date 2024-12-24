package com.todomanager.to_do_manager_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * Security configuration for managing authentication and CSRF in the application.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Define public endpoints
                .requestMatchers("/public/**", "/login", "/login/oauth2/code/**").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                // Configure CSRF handling
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            // Add custom CSRF filter after BasicAuthenticationFilter
            .addFilterAfter(new CookieCsrfFilter(), BasicAuthenticationFilter.class)
            .oauth2Login(oauth2 -> oauth2
                // Redirect to the React frontend after successful login
                .defaultSuccessUrl("http://localhost:3000", true)
            );

        return http.build();
    }
}