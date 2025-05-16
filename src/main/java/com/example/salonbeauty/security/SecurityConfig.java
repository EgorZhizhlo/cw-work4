// src/main/java/com/example/salonbeauty/security/SecurityConfig.java
package com.example.salonbeauty.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        var prov = new DaoAuthenticationProvider();
        prov.setUserDetailsService(userDetailsService);
        prov.setPasswordEncoder(passwordEncoder());
        return prov;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // отключаем CSRF, т.к. стейтлесс и JWT
                .csrf(AbstractHttpConfigurer::disable)

                // без сессий, весь контекст хранится в JWT
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // подключаем наш UserDetailsService + PasswordEncoder
                .authenticationProvider(daoAuthenticationProvider())

                // правила доступа
                .authorizeHttpRequests(auth -> auth
                        // публичные страницы
                        .requestMatchers(
                                "/",
                                "/about",
                                "/services",              // список услуг
                                "/services/*",            // детали услуги: /services/{id}
                                "/register", "/login",    // формы регистрации и логина
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()

                        // бронирование услуги по GET /services/{id}/book
                        .requestMatchers("/services/*/book").authenticated()

                        // профиль пользователя и изменения (GET /profile, POST /profile)
                        .requestMatchers("/profile/**").authenticated()

                        // управление своими бронированиями (GET и POST в /profile/bookings/**)
                        .requestMatchers("/profile/bookings/**").authenticated()

                        // только для админа
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // всё остальное — аутентификация
                        .anyRequest().authenticated()
                )

                // наш фильтр вытаскивает JWT из cookie и кладёт в SecurityContext
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
