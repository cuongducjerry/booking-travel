package vn.travel.booking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import vn.travel.booking.entity.RestResponse;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public CustomAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setError(accessDeniedException.getMessage());
        res.setMessage(accessDeniedException.getMessage());

        mapper.writeValue(response.getWriter(), res);
    }
}
