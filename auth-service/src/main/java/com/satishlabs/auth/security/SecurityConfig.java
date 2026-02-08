package com.satishlabs.auth.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.satishlabs.auth.config.OAuth2SuccessHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    // Public GET endpoints for puja/pandit catalog
    // Using string patterns instead of RequestMatcher to avoid compilation issues

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/user/profile-pic",
                            "/api/user/profile-pic"
                    ).permitAll()
                    .requestMatchers("/auth/**", "/api/auth/**", "/oauth2/**", "/login/**").permitAll()
                    // Public catalog endpoints (with and without /api prefix for monolith context-path)
                    .requestMatchers("/puja", "/puja/**", "/api/puja", "/api/puja/**").permitAll()
                    .requestMatchers("/pandit", "/pandit/**", "/api/pandit", "/api/pandit/**").permitAll()
                    // Public GET for items (admin CRUD at /admin/items)
                    .requestMatchers(HttpMethod.GET, "/items", "/items/**", "/api/items", "/api/items/**").permitAll()
                    // Saga: order-service calls these without user JWT
                    .requestMatchers("/pandit/reserve", "/pandit/release", "/api/pandit/reserve", "/api/pandit/release").permitAll()
                    // Order endpoints require authentication
                    .requestMatchers("/orders/**", "/api/orders/**").authenticated()
                    // Admin endpoints require ADMIN role
                    .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )


            .oauth2Login(oauth -> oauth
                .successHandler(oAuth2SuccessHandler)
            )

            .sessionManagement(sess ->
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .addFilterBefore(new XUserIdFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            // 🔥 Prevent 302 redirect, return 401 instead
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                )
            );

        return http.build();
    }

    // 🔥 THIS IS MANDATORY
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "https://www.inxinfo.com",
                "https://inxinfo.com",
                "https://inxinfo-user-portal-1.onrender.com"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}


