package vn.travel.booking.util.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.travel.booking.entity.RestResponse;

@RestControllerAdvice
public class GlobalException {

    private ResponseEntity<RestResponse<Object>> build(HttpStatus status, String message) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(status.value());
        res.setError(status.getReasonPhrase());
        res.setMessage(message);
        return ResponseEntity.status(status).body(res);
    }

    @ExceptionHandler({
            NameInvalidException.class,
            InvalidPasswordException.class
    })
    public ResponseEntity<RestResponse<Object>> handleBusiness(Exception ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            UnauthenticatedException.class
    })
    public ResponseEntity<RestResponse<Object>> handleAuth(Exception ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<RestResponse<Object>> handleDisabled(Exception ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            IdInvalidException.class
    })
    public ResponseEntity<RestResponse<Object>> handleNotFound(Exception ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
            PermissionNotFoundException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<RestResponse<Object>> handleBadRequest(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<RestResponse<Object>> handleForbidden(Exception ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<RestResponse<Object>> handleOther(Exception ex) {
//        // log.error("Unexpected error", ex)
//        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
//    }


}
