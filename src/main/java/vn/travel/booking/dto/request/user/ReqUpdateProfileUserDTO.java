package vn.travel.booking.dto.request.user;

import lombok.Data;

@Data
public class ReqUpdateProfileUserDTO {
    private long id;
    private String fullName;
    private String phone;
    private String bio;
    private String address;
    private int age;
}
