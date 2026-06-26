package com.example.carstore.util;

import org.springframework.security.core.Authentication;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static boolean isLoggedIn(Authentication auth) {
        return auth != null && auth.isAuthenticated();
    }

    public static String username(Authentication auth) {
        return isLoggedIn(auth) ? auth.getName() : null;
    }

    public static boolean isAdmin(Authentication auth) {
        return isLoggedIn(auth)
                && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
