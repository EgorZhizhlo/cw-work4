// SecurityConfig.java
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
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Полностью отключаем CSRF, т.к. аутентификация по JWT
                .csrf(AbstractHttpConfigurer::disable)

                // Сессий нет — всё stateless
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Наш Dao-провайдер
                .authenticationProvider(daoAuthenticationProvider())

                // Правила доступа
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/register",
                                "/css/**", "/js/**", "/about",
                                "/images/**", "/static/**", "/services"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/booking/**").hasRole("CLIENT")
                        .requestMatchers("/profile/**").authenticated()
                        .anyRequest().authenticated()
                )

                // И запускаем наш JWT-фильтр ДО стандартного UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
