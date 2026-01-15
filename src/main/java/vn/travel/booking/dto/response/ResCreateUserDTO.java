package vn.travel.booking.dto.response;

import lombok.*;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private int age;

}
