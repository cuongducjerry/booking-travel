package vn.travel.booking.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import vn.travel.booking.util.SecurityUtil;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return SecurityUtil.getCurrentUserLogin(); // email/username
    }
}
