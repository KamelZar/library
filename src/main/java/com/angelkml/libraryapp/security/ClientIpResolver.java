package com.angelkml.libraryapp.security;

import jakarta.servlet.http.HttpServletRequest;

final class ClientIpResolver {

    private ClientIpResolver() {
    }

    static String resolve(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
