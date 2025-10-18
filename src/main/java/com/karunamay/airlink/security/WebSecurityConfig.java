package com.karunamay.airlink.security;

import com.karunamay.airlink.service.user.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.frontEndClient}")
    private String frontendClient;

    @Value("${app.cors.swaggerClient}")
    private String swaggerClient;

    @Value("${app.cors.swaggerServerUrl}")
    private String swaggerServerUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of(frontendClient, swaggerClient, swaggerServerUrl)
        );
        configuration.setAllowedMethods(List.of("GET", "PUT", "PATCH", "DELETE", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfiguration()))
                .authorizeHttpRequests(
                        auth -> auth
                                // public endpoints
                                .requestMatchers(
                                        "/user/register",
                                        "/user/login",
                                        "/user/logout",
                                        "/user/forgot-password",
                                        "/user/refresh-token",
                                        "/user/check-auth",
                                        "/docs",
                                        "/api-docs"

                                )
                                .permitAll()
                                .requestMatchers(
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/swagger-resources/**",
                                        "/webjars/**"
                                ).permitAll()
                                // Admin only endpoints
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()

                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((
                                request,
                                response,
                                authEx) -> {
                            response.setContentType(APPLICATION_JSON);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(
                                    "{\"error\": \"Unauthorized\", \"message\": \"" +
                                            authEx.getMessage() + "\"}");
                        })
                        .accessDeniedHandler((
                                request,
                                response,
                                accessDeniedException) -> {
                            response.setContentType("Application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write(
                                    "{\"error\": \"Forbidden\", \"message\": \"" +
                                            accessDeniedException.getMessage() + "\"}");
                        }));
        return http.build();
    }
}
