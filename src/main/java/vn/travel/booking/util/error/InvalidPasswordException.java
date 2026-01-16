package vn.travel.booking.util.error;

public class InvalidPasswordException extends Exception {
    public InvalidPasswordException(String message){
        super(message);
    }
}
