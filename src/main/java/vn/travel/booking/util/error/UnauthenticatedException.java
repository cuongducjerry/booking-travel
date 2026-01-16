package vn.travel.booking.util.error;

public class UnauthenticatedException extends Exception{
    public UnauthenticatedException(String message){
        super(message);
    }
}
