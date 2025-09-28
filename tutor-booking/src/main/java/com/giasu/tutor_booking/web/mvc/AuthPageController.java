package com.giasu.tutor_booking.web.mvc;

import com.giasu.tutor_booking.domain.user.UserRole;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {

    private static final UserRole[] SELF_SIGNUP_ROLES = {
            UserRole.PARENT,
            UserRole.STUDENT,
            UserRole.TUTOR
    };

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Dang nhap");
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Dang ky");
        model.addAttribute("roles", SELF_SIGNUP_ROLES);
        model.addAttribute("defaultRole", UserRole.PARENT);
        return "auth/register";
    }
}