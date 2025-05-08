package com.example.salonbeauty.repository;

import com.example.salonbeauty.model.User;
import com.example.salonbeauty.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Поиск по username/email для аутентификации
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    List<User> findAllByRole_Name(String roleName);

    // Методы для статистики
    long countByRoleName(String roleName);
    long countByRole(Role role);
}
