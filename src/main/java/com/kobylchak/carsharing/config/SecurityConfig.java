package com.kobylchak.carsharing.config;

import com.kobylchak.carsharing.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] AUTH_WHITELIST = {
            "api/v3/api-docs/**",
            "api/swagger-ui/**",
            "api/swagger-ui.html",
            "api/swagger/**",
            "api/auth/**",
            "error",
            "api/cars",
            "swagger-ui.html",
            "v3/api-docs/**",
            "swagger-ui/**",
            "webjars/swagger-ui/**"
            // other public endpoints of your API may be appended to this array
    };
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                       .cors(AbstractHttpConfigurer::disable)
                       .csrf(AbstractHttpConfigurer::disable)
                       .authorizeHttpRequests(
                               auth -> auth
                                               .requestMatchers(AUTH_WHITELIST)
                                               .permitAll()
                                               .anyRequest()
                                               .authenticated()
                                             )
                       .httpBasic(Customizer.withDefaults())
                       .sessionManagement(session -> session.sessionCreationPolicy(
                               SessionCreationPolicy.STATELESS))
                       .addFilterBefore(jwtAuthenticationFilter,
                                        UsernamePasswordAuthenticationFilter.class)
                       .userDetailsService(userDetailsService)
                       .build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
