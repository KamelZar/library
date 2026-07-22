package com.angelkml.libraryapp.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;
    private final AuthenticationFailureHandler delegate = new SimpleUrlAuthenticationFailureHandler("/login.html?error");

    public AuthFailureHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                         AuthenticationException exception) throws IOException, ServletException {
        loginAttemptService.loginFailed(ClientIpResolver.resolve(request));
        delegate.onAuthenticationFailure(request, response, exception);
    }
}
