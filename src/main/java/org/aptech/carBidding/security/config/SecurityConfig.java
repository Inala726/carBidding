package org.aptech.carBidding.security.config;

import lombok.RequiredArgsConstructor;
import org.aptech.carBidding.security.jwt.JwtAuthenticationFilter;
import org.aptech.carBidding.security.jwt.JwtAuthorizationFilter;
import org.aptech.carBidding.security.jwt.JwtTokenProvider;
import org.aptech.carBidding.security.user.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authManager) throws Exception {
        JwtAuthenticationFilter authFilter =
                new JwtAuthenticationFilter(authManager, jwtTokenProvider);
        authFilter.setFilterProcessesUrl("/api/v1/auth/login");

        http
                .cors().and()
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // public endpoints
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password"
                        ).permitAll()

                        // anyone logged in can view auctions
                        .requestMatchers(HttpMethod.GET, "/api/v1/auctions/**").authenticated()

                        // any authenticated user can fetch their own profile
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/v1/auctions/bids/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auctions/me/won").authenticated()


                        // bidders may place bids
                        .requestMatchers(HttpMethod.POST, "/api/v1/auctions/*/bids")
                        .hasRole("BIDDER")

                        // sellers or admins can create, update, delete auctions
                        .requestMatchers(HttpMethod.POST,   "/api/v1/auctions").hasAnyRole("ADMIN", "BIDDER")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/auctions/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/auctions/**").hasAnyRole("ADMIN")

                        // everything else requires ADMIN
                        .anyRequest().hasRole("ADMIN")
                )
                // add our JWT filters
                .addFilter(authFilter)
                .addFilterBefore(
                        new JwtAuthorizationFilter(jwtTokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8081"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
