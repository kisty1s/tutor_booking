package com.giasu.tutor_booking.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TutorDirectoryController {

    @GetMapping("/tutors")
    public String tutors(Model model) {
        model.addAttribute("pageTitle", "Danh s?ch gia s? (?ang ph?t tri?n)");
        return "tutors/index";
    }
}
