package com.example.devjobs.config;

import com.example.devjobs.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Public GET endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/job/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/company/**").permitAll()

                        // EMPLOYER-only endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/company/**").hasAnyRole("ADMIN", "EMPLOYER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/company/**").hasAnyRole("ADMIN", "EMPLOYER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/company/**").hasAnyRole("ADMIN", "EMPLOYER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/job/**").hasAnyRole("ADMIN", "EMPLOYER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/job/**").hasAnyRole("ADMIN", "EMPLOYER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/job/**").hasAnyRole("ADMIN", "EMPLOYER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/app/**").hasAnyRole("ADMIN", "EMPLOYER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/app/getApplicationsByJobId/**").hasAnyRole("ADMIN", "EMPLOYER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/app/getApplicationsEventsId/**").hasAnyRole("ADMIN", "EMPLOYER")

                        // CANDIDATE + ADMIN endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/app/**").hasAnyRole("ADMIN", "CANDIDATE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/app/getApplications/me").hasAnyRole("ADMIN", "CANDIDATE")

                        // ADMIN-only endpoints
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // Authenticated users (GET for jobs, companies, etc.)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
