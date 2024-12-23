package com.example.project_spring.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtTokenAuthenticationFilter extends GenericFilterBean
{
    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String token = jwtTokenProvider.resolveToken(request);

        System.out.println("Incoming request to: " + request.getRequestURI());
        System.out.println("Resolved token: " + token);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);

                if (auth != null) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("Authentication successful for user: " + auth.getName());
                } else {
                    System.out.println("Authentication failed: auth is null.");
                }
            } else {
                System.out.println("Invalid or missing token.");
            }
        } catch (InvalidJwtAuthenticationException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during JWT processing: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(req, res);
    }
}
