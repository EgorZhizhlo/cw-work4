package com.example.salonbeauty.service;

import com.example.salonbeauty.model.User;

public interface AuthService {
    User register(String username, String email, String rawPassword,
                  String firstName, String lastName);

    String login(String username, String rawPassword);

    boolean validateToken(String token);

    String extractUsername(String token);

    /** возвращает APP_JWT_EXPIRATION_MS */
    long getExpirationMs();
}
