// src/main/java/com/example/salonbeauty/controller/ProfileController.java
package com.example.salonbeauty.controller;

import com.example.salonbeauty.model.User;
import com.example.salonbeauty.service.BookingService;
import com.example.salonbeauty.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserService    userService;
    private final BookingService bookingService;

    @GetMapping
    public String profile(Model model, Principal principal) {
        User user = userService.getByUsername(principal.getName());

        // если ADMIN — редирект на админку
        if ("ADMIN".equals(user.getRole().getName())) {
            return "redirect:/admin";
        }

        model.addAttribute("user", user);

        // для мастера и клиента список берём по user.getId()
        if ("MASTER".equals(user.getRole().getName())) {
            model.addAttribute("appointments",
                    bookingService.listMasterAppointments(user.getId()));
            model.addAttribute("viewType", "master");
        } else {
            model.addAttribute("appointments",
                    bookingService.listClientAppointments(user.getId()));
            model.addAttribute("viewType", "client");
        }

        return "profile";
    }

    @PostMapping
    public String updateProfile(@ModelAttribute("user") User form, Principal principal) {
        User current = userService.getByUsername(principal.getName());
        userService.updateProfile(
                current.getId(),
                form.getEmail(),
                form.getFirstName(),
                form.getLastName()
        );
        return "redirect:/profile";
    }
}
