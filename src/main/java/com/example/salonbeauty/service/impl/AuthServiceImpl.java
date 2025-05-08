package com.example.salonbeauty.service.impl;

import com.example.salonbeauty.model.User;
import com.example.salonbeauty.model.Role;
import com.example.salonbeauty.repository.RoleRepository;
import com.example.salonbeauty.repository.UserRepository;
import com.example.salonbeauty.service.AuthService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // Вспомогательный метод: строит HMAC-ключ из plain-text секрета
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public User register(String username, String email, String rawPassword,
                         String firstName, String lastName) {
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Role CLIENT not found"));

        User u = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .firstName(firstName)
                .lastName(lastName)
                .role(clientRole)
                .build();

        return userRepository.save(u);
    }

    @Override
    public String login(String username, String rawPassword) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(rawPassword, u.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        // Используем Key вместо передачи строки, чтобы JJWT не декодировал ваш секрет
        return Jwts.builder()
                .setSubject(u.getUsername())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public long getExpirationMs() {
        return jwtExpirationMs;
    }
}
