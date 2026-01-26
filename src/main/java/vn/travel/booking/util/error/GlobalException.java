package vn.travel.booking.util.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.travel.booking.entity.RestResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    private ResponseEntity<RestResponse<Object>> build(
            HttpStatus status, String message, Object data
    ) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(status.value());
        res.setError(status.getReasonPhrase());
        res.setMessage(message);
        res.setData(data);
        return ResponseEntity.status(status).body(res);
    }

    private ResponseEntity<RestResponse<Object>> build(
            HttpStatus status, String message
    ) {
        return build(status, message, null);
    }

    // HANDLE VALIDATION
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(
                        err.getField(),
                        err.getDefaultMessage()
                ));

        return build(
                HttpStatus.BAD_REQUEST,
                "Dữ liệu không hợp lệ",
                errors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestResponse<Object>> handleEnumError(
            HttpMessageNotReadableException ex
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Giá trị status không hợp lệ"
        );
    }

    @ExceptionHandler({
            NameInvalidException.class,
            InvalidPasswordException.class,
            BusinessException.class
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

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<RestResponse<Object>> handleLocked(LockedException ex) {
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
            IllegalArgumentException.class,
            ImageException.class
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
