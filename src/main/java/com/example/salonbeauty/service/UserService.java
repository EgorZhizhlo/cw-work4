package com.example.salonbeauty.service;

import com.example.salonbeauty.model.User;

public interface UserService {
    User getById(Long id);
    User getByUsername(String username);

    /**
     * Обновить email, имя и фамилию пользователя.
     * @return обновлённый объект User
     */
    User updateProfile(Long id, String email, String firstName, String lastName);

    /**
     * Сменить пароль (проверяя старый).
     */
    void changePassword(Long id, String oldPassword, String newPassword);
}
