package com.giasu.tutor_booking.security;

import com.giasu.tutor_booking.domain.user.UserAccount;
import com.giasu.tutor_booking.domain.user.UserRole;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAccountPrincipal implements UserDetails {

    private final UserAccount userAccount;

    public UserAccountPrincipal(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole role = userAccount.getRole();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return userAccount.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return userAccount.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
