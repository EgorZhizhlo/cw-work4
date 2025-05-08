package com.example.salonbeauty.controller.admin;

import com.example.salonbeauty.model.*;
import com.example.salonbeauty.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("countUsers",    adminService.countUsers());
        model.addAttribute("countServices", adminService.countServices());
        model.addAttribute("countAppts",    adminService.countAllAppointments());
        model.addAttribute("countBooked",   adminService.countAppointmentsByStatus(AppointmentStatus.BOOKED));
        model.addAttribute("countCanceled", adminService.countAppointmentsByStatus(AppointmentStatus.CANCELLED));
        return "admin/dashboard";
    }

    // ————————————————————————————
    // Пользователи
    // ————————————————————————————
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", adminService.listUsers());
        return "admin/users";  // templates/admin/users.html
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user;
        if (id == 0) {
            user = new User();
            // чтобы в Thymeleaf проверка на новый шла по null, оставляем id = null
        } else {
            user = adminService.getUser(id);
        }
        model.addAttribute("user", user);
        model.addAttribute("roles", adminService.listRoles());
        return "admin/user-form";
    }

    // Обработка формы и сохранение (и нового, и отредактированного)
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user) {
        if (user.getId() != null) {
            User persisted = adminService.getUser(user.getId());
            user.setPasswordHash(persisted.getPasswordHash());
        }
        adminService.updateUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return "redirect:/admin/users";
    }

    // ————————————————————————————
    // Услуги
    // ————————————————————————————
    @GetMapping("/services")
    public String listServices(Model model) {
        model.addAttribute("services", adminService.listServices());
        return "admin/services";
    }

    @GetMapping("/services/edit/{id}")
    public String editService(@PathVariable Long id, Model model) {
        model.addAttribute("service", adminService.getService(id));
        model.addAttribute("masters", adminService.listMasters());
        return "admin/service-form";
    }

    @GetMapping("/services/new")
    public String newService(Model model) {
        model.addAttribute("service", new ServiceEntity());
        model.addAttribute("masters", adminService.listMasters());
        return "admin/service-form";
    }

    @PostMapping("/services/save")
    public String saveService(@ModelAttribute ServiceEntity service) {
        adminService.saveService(service);
        return "redirect:/admin/services";
    }

    @PostMapping("/services/delete/{id}")
    public String deleteService(@PathVariable Long id) {
        adminService.deleteService(id);
        return "redirect:/admin/services";
    }

    // ————————————————————————————
    // Записи
    // ————————————————————————————
    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        model.addAttribute("appointments", adminService.listAppointments());
        return "admin/appointments";
    }

    @PostMapping("/appointments/status/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam AppointmentStatus status) {
        adminService.updateAppointmentStatus(id, status);
        return "redirect:/admin/appointments";
    }

    @PostMapping("/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable Long id) {
        adminService.deleteAppointment(id);
        return "redirect:/admin/appointments";
    }
}
