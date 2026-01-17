package vn.travel.booking.dto.response.user;

import lombok.Data;

import java.time.Instant;

@Data
public class ResUpdateProfileUserDTO {
    private long id;
    private String fullName;
    private String phone;
    private String bio;
    private String address;
    private int age;
    private Instant updatedAt;
}
