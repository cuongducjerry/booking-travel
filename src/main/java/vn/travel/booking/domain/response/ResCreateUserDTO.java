package vn.travel.booking.domain.response;

import lombok.*;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String age;

}
