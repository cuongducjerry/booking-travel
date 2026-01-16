package vn.travel.booking.config;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.travel.booking.entity.User;
import vn.travel.booking.util.constant.StatusUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // ================== ACCOUNT STATUS ==================

    @Override
    public boolean isEnabled() {
        if (user.getStatus() == StatusUser.PENDING) {
            throw new DisabledException("Tài khoản đang chờ admin duyệt");
        }
        return user.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        if (user.getStatus() == StatusUser.LOCK) {
            throw new LockedException("Tài khoản của bạn đang bị khóa");
        }
        return user.isActive();
    }

    // ================== BASIC INFO ==================

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // ================== AUTHORITIES ==================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();

        // PERMISSIONS
        user.getRole().getPermissions().forEach(permission ->
                authorities.add(
                        new SimpleGrantedAuthority(permission.getCode())
                )
        );

        return authorities;
    }

    // ================== HELPER ==================

    public boolean hasAuthority(String authority) {
        return getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    public Long getId() {
        return user.getId();
    }

    // ================== DEFAULT ==================

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}

