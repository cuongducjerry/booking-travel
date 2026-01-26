package vn.travel.booking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import vn.travel.booking.entity.User;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.util.error.NameInvalidException;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    private final UserRepository userRepository;

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) return null;

        String username = auth.getName();

        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new NameInvalidException("User not found");
        }
        Long userId = user.getId();

        return () -> userId.toString(); // PRINCIPAL = userId
    }
}

