package org.droid.zero.multitenantaipayrollsystem.security.config;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${api.endpoint.base-url}")
    private String baseUrl;

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;
    private final CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;
    private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;

    /**
     * CHAIN 1: Basic Auth for Auth Endpoints
     * Matches only URLs starting with /auth/**
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(baseUrl + "/auth/login")
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(customBasicAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .build();
    }

    /**
     * CHAIN 2: JWT Auth for Everything Else
     * Matches any request not handled by the first chain
     */
    @Bean
    @Order(2)
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customBearerTokenAuthenticationEntryPoint)
                        .accessDeniedHandler(customBearerTokenAccessDeniedHandler)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(getPublicEndpoints().toArray(String[]::new)).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .build();
    }

    private List<String> getPublicEndpoints() {
        return List.of(
                "/scalar/**",
                "/v3/api-docs",
                baseUrl + "/auth/**"
        );
    }
}
