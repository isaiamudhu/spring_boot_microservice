package com.example.inventoryservice.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.inventoryservice.service.TokenValidationService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@Component
public class JwtValidationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenValidationService tokenValidationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("token is "+ authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            if (!tokenValidationService.validateTokenWithUserService(token)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        request.getSession(false);
        response.setHeader("Set-Cookie", "");

        filterChain.doFilter(request, response);
    }
}
