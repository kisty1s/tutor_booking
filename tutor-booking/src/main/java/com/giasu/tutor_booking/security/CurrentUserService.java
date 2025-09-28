package com.giasu.tutor_booking.security;

import com.giasu.tutor_booking.domain.user.UserAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    public UserAccount getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserAccountPrincipal principal)) {
            throw new IllegalStateException("No authenticated user available");
        }
        return principal.getUserAccount();
    }
}