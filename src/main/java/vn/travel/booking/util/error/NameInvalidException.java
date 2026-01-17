package vn.travel.booking.util.error;

public class NameInvalidException extends RuntimeException {
    public NameInvalidException(String message){
        super(message);
    }
}
