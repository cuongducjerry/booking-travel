package vn.travel.booking.config;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.travel.booking.entity.User;
import vn.travel.booking.util.constant.StatusUser;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

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

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().getName()
        ));
    }

    // các method còn lại
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}
