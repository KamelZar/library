package com.angelkml.libraryapp.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class IpBlacklistFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttemptService;

    public IpBlacklistFilter(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String ip = ClientIpResolver.resolve(request);
        if (loginAttemptService.isBlacklisted(ip)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Too many failed login attempts, try again later");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
