package com.example.salonbeauty.security;

import org.springframework.http.ResponseCookie;

public final class CookieUtil {
    private CookieUtil() {}

    public static ResponseCookie createJwtCookie(String token, long maxAgeSec, boolean secure) {
        return ResponseCookie.from("JWT", token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAgeSec)
                .sameSite("Strict")
                .build();
    }

    public static ResponseCookie deleteJwtCookie(boolean secure) {
        return ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
