package org.aptech.carBidding.security.config;

import lombok.RequiredArgsConstructor;
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
import org.aptech.carBidding.exceptions.RestAccessDeniedHandler;
import org.aptech.carBidding.exceptions.RestAuthenticationEntryPoint;
import org.aptech.carBidding.exceptions.RestAuthenticationFailureHandler;


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authManager) throws Exception {
        JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(authManager, jwtTokenProvider);
        authFilter.setFilterProcessesUrl("/api/v1/auth/login");

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password"
                        ).permitAll()
                        // bidders can place bids
                        .requestMatchers(HttpMethod.POST, "/api/v1/auctions/*/bids").hasRole("BIDDER")
                        // sellers can create auctions
                        .requestMatchers(HttpMethod.POST, "/api/v1/auctions").hasRole("SELLER")
                        // admins get everything else
                        .anyRequest().hasRole("ADMIN")
                )
                .addFilter(authFilter)
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
