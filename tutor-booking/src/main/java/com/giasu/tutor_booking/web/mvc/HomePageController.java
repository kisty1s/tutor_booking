package com.giasu.tutor_booking.web.mvc;

import com.giasu.tutor_booking.dto.subject.SubjectResponse;
import com.giasu.tutor_booking.service.SubjectService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final SubjectService subjectService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<SubjectResponse> featuredSubjects = subjectService.listSubjects().stream()
                .limit(6)
                .toList();
        model.addAttribute("pageTitle", "N?n t?ng k?t n?i gia s? chuy?n nghi?p");
        model.addAttribute("featuredSubjects", featuredSubjects);
        return "index";
    }
}
