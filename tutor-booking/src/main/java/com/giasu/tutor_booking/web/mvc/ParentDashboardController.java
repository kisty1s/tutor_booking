package com.giasu.tutor_booking.web.mvc;

import com.giasu.tutor_booking.dto.user.ParentProfileResponse;
import com.giasu.tutor_booking.service.ParentProfileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ParentDashboardController {

    private final ParentProfileService parentProfileService;

    @GetMapping("/parents/dashboard")
    public String dashboard(Model model) {
        List<ParentProfileResponse> parents = parentProfileService.listParentProfiles();
        model.addAttribute("pageTitle", "T?ng quan ph? huynh");
        model.addAttribute("parents", parents);
        return "parents/dashboard";
    }
}
