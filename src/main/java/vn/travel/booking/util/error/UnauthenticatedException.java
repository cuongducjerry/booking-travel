package vn.travel.booking.util.error;

public class UnauthenticatedException extends RuntimeException{
    public UnauthenticatedException(String message){
        super(message);
    }
}
