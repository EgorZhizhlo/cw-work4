// src/main/java/com/example/salonbeauty/controller/BookingController.java
package com.example.salonbeauty.controller;

import com.example.salonbeauty.model.Appointment;
import com.example.salonbeauty.model.User;
import com.example.salonbeauty.service.BookingService;
import com.example.salonbeauty.service.SalonService;
import com.example.salonbeauty.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final SalonService salonService;
    private final UserService userService;

    @GetMapping("/{serviceId}")
    public String showBookingForm(
            @PathVariable Long serviceId,
            @RequestParam(required = false) Long masterId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model
    ) {
        if (date == null) date = LocalDate.now();
        model.addAttribute("date", date);

        model.addAttribute("service", salonService.getById(serviceId));

        List<User> masters = bookingService.listAllMasters();
        model.addAttribute("masters", masters);

        if (masterId != null) {
            model.addAttribute("selectedMasterId", masterId);
            User m = bookingService.getMasterById(masterId);
            List<LocalTime> slots = bookingService.getAvailableSlots(serviceId, m, date);
            model.addAttribute("slots", slots);
        }
        return "booking-form";
    }

    @PostMapping
    public String processBooking(
            @RequestParam Long serviceId,
            @RequestParam Long masterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            Principal principal
    ) {
        User client = userService.getByUsername(principal.getName());
        bookingService.book(client.getId(), masterId, serviceId, date, time);
        return "redirect:/profile";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, Principal principal) {
        bookingService.cancelBooking(id, principal.getName());
        return "redirect:/profile";
    }
}
