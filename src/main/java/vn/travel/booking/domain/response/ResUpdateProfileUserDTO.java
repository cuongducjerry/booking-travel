package vn.travel.booking.domain.response;

import lombok.Data;

import java.time.Instant;

@Data
public class ResUpdateProfileUserDTO {
    private long id;
    private String fullName;
    private String phone;
    private String bio;
    private String address;
    private String age;
    private Instant updatedAt;
}
