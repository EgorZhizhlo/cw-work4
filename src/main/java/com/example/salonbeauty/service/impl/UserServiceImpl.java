package com.example.salonbeauty.service.impl;

import com.example.salonbeauty.model.User;
import com.example.salonbeauty.repository.UserRepository;
import com.example.salonbeauty.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public User updateProfile(Long id, String email, String firstName, String lastName) {
        User u = getById(id);
        u.setEmail(email);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        return userRepository.save(u);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User u = getById(id);
        if (!passwordEncoder.matches(oldPassword, u.getPasswordHash())) {
            throw new RuntimeException("Old password mismatch");
        }
        u.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(u);
    }
}
