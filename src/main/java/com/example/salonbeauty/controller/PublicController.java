package com.example.salonbeauty.controller;

import com.example.salonbeauty.model.ServiceEntity;
import com.example.salonbeauty.model.User;
import com.example.salonbeauty.service.AuthService;
import com.example.salonbeauty.service.BookingService;
import com.example.salonbeauty.service.SalonService;
import com.example.salonbeauty.service.StatisticsService;
import com.example.salonbeauty.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PublicController {

    private final AuthService authService;
    private final SalonService salonService;
    private final StatisticsService statsService;
    private final BookingService bookingService;
    private final UserService userService;  // для получения clientId по username

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("clientsCount", statsService.countClients());
        model.addAttribute("mastersCount", statsService.countMasters());
        model.addAttribute("servicesCount", statsService.countServices());
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    /** Список всех услуг */
    @GetMapping("/services")
    public String listServices(Model model) {
        model.addAttribute("services", salonService.listAll());
        return "services";
    }

    /** Детали услуги: описание, выбор мастера и свободные слоты */
    @GetMapping("/services/{id}")
    public String serviceDetails(
            @PathVariable("id") Long serviceId,
            @RequestParam(value = "masterId", required = false) Long masterId,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model,
            Principal principal
    ) {
        if (date == null) date = LocalDate.now();

        ServiceEntity service = salonService.getById(serviceId);
        model.addAttribute("service", service);
        model.addAttribute("isAuthenticated", principal != null);
        model.addAttribute("date", date);

        List<User> masters = bookingService.listAllMasters();
        model.addAttribute("masters", masters);

        if (masterId == null && !masters.isEmpty()) {
            masterId = masters.get(0).getId();
        }
        model.addAttribute("selectedMasterId", masterId);

        if (masterId != null) {
            User m = bookingService.getMasterById(masterId);
            List<LocalTime> slots = bookingService.getAvailableSlots(serviceId, m, date);
            model.addAttribute("slots", slots);
        }

        return "service-detail";
    }

    /** Эндпоинт создания новой записи (бронирования) */
    @GetMapping("/services/{id}/book")
    public String bookAppointment(
            @PathVariable("id") Long serviceId,
            @RequestParam("masterId") Long masterId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("at")   // <- поменяли имя параметра
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            Principal principal
    ) {
        Long clientId = userService.getByUsername(principal.getName()).getId();
        bookingService.book(clientId, masterId, serviceId, date, time);
        return "redirect:/profile";
    }

}
