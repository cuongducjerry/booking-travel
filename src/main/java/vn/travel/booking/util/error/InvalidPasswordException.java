package vn.travel.booking.util.error;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message){
        super(message);
    }
}
