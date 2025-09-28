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
public class SubjectPageController {

    private final SubjectService subjectService;

    @GetMapping("/subjects")
    public String subjects(Model model) {
        List<SubjectResponse> subjects = subjectService.listSubjects();
        model.addAttribute("pageTitle", "Danh s?ch m?n h?c");
        model.addAttribute("subjects", subjects);
        return "subjects/index";
    }
}
