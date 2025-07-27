package com.springbootjwtauth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootjwtauth.dto.response.ErrorResponse;
import com.springbootjwtauth.exception.ErrorCode;
import com.springbootjwtauth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/v1/auth/signup", "/api/v1/auth/login", "/api/v1/auth/users").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                // 인증 실패(토큰 없거나 INVALID_TOKEN) 시
                .authenticationEntryPoint((req, res, authEx) -> {
                    ErrorResponse body = new ErrorResponse(
                            ErrorCode.INVALID_TOKEN.getCode(),
                            ErrorCode.INVALID_TOKEN.getMessage()
                    );
                    res.setStatus(HttpStatus.UNAUTHORIZED.value());
                    res.setCharacterEncoding("UTF-8");
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write(new ObjectMapper().writeValueAsString(body));
                })
                // 권한 부족 시
                .accessDeniedHandler((req, res, accessDeniedEx) -> {
                    ErrorResponse body = new ErrorResponse(
                            ErrorCode.ACCESS_DENIED.getCode(),
                            ErrorCode.ACCESS_DENIED.getMessage()
                    );
                    res.setStatus(HttpStatus.FORBIDDEN.value());
                    res.setCharacterEncoding("UTF-8");
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write(new ObjectMapper().writeValueAsString(body));
                })
        )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // 필터 등록

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
