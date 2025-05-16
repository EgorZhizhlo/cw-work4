package com.example.salonbeauty.controller;

import com.example.salonbeauty.service.AuthService;
import com.example.salonbeauty.security.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверные имя или пароль");
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        String token = authService.login(username, password);
        long maxAge = authService.getExpirationMs() / 1000;
        boolean secure = request.isSecure();

        ResponseCookie cookie = CookieUtil.createJwtCookie(token, maxAge, secure);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String username,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String firstName,
                                  @RequestParam String lastName,
                                  Model model) {
        try {
            authService.register(username, email, password, firstName, lastName);
            model.addAttribute("message", "Регистрация успешна! Теперь войдите.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {
        // Очистим контекст, JWT-фильтр пропустит незалогиненного
        // (или можно явно делать SecurityContextHolder.clearContext())
        boolean secure = request.isSecure();
        ResponseCookie cookie = CookieUtil.deleteJwtCookie(secure);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return "redirect:/login?logout";
    }
}