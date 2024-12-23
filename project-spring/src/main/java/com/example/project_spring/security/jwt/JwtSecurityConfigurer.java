package com.example.project_spring.security.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private JwtTokenProvider jwtTokenProvider;

    public JwtSecurityConfigurer(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtTokenAuthenticationFilter customFilter = new JwtTokenAuthenticationFilter(jwtTokenProvider);

        http
                 // Disable CSRF if you're using JWTs
                .authorizeHttpRequests(authorize ->
                        authorize
                                .anyRequest().authenticated() // Adjust authorization as needed
                )
                .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );
    }
}
