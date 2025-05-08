// src/main/java/com/example/salonbeauty/config/DataInitializer.java
package com.example.salonbeauty.config;

import com.example.salonbeauty.model.Role;
import com.example.salonbeauty.model.User;
import com.example.salonbeauty.repository.RoleRepository;
import com.example.salonbeauty.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1) создаём роли CLIENT, MASTER, ADMIN если ещё нет
        List<String> names = List.of("CLIENT", "MASTER", "ADMIN");
        for (String name : names) {
            roleRepo.findByName(name)
                    .orElseGet(() -> roleRepo.save(Role.builder().name(name).build()));
        }

        // 2) создаём системного admin, если нет
        userRepo.findByUsername("admin").orElseGet(() -> {
            Role adminRole = roleRepo.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("Role ADMIN not found"));

            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .passwordHash(passwordEncoder.encode("adminpass"))
                    .firstName("System")
                    .lastName("Administrator")
                    .role(adminRole)
                    .build();

            return userRepo.save(admin);
        });
    }
}
