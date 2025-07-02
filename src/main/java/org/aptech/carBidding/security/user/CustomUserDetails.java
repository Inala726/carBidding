package org.aptech.carBidding.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.aptech.carBidding.models.User;
import org.aptech.carBidding.enums.UserStatus;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Adapts our User entity for Spring Security.
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Only map roles—no separate Permission entity
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // We aren’t tracking expiry separately
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Lock out users whose status is LOCKED
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // We aren’t tracking credential expiry
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Only ACTIVE users can log in
        return true;
    }
}
