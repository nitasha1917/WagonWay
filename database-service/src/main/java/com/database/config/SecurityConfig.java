package com.database.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow all POST requests for /api/users for registration
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // Allow GET requests for user email check without authentication
                        .requestMatchers(HttpMethod.GET, "/api/users/email/**").permitAll()
                        // Permit all other requests for simplicity
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
