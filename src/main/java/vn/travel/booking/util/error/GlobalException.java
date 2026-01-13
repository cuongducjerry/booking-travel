package vn.travel.booking.util.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.travel.booking.domain.RestResponse;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            IdInvalidException.class,
            BadCredentialsException.class,
    })
    public ResponseEntity<RestResponse<Object>> handleIdException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(ex.getMessage());
        res.setError("Exception occurs...");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // Other exception
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<RestResponse<Object>> handleOtherExceptions(Exception ex) {
//        RestResponse<Object> res = new RestResponse<>();
//        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
//        res.setError("Exception occurs...");
//        res.setMessage(ex.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
//    }

}
