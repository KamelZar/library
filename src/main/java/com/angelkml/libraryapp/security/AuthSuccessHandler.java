package com.angelkml.libraryapp.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginAttemptService loginAttemptService;
    private final AuthenticationSuccessHandler delegate = new SimpleUrlAuthenticationSuccessHandler("/index.html");

    public AuthSuccessHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        loginAttemptService.loginSucceeded(ClientIpResolver.resolve(request));
        delegate.onAuthenticationSuccess(request, response, authentication);
    }
}
