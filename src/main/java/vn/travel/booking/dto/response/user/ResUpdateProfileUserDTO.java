package vn.travel.booking.dto.response.user;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class ResUpdateProfileUserDTO {
    private Long id;
    private String fullName;
    private String phone;
    private String bio;
    private String address;
    private LocalDate dateOfBirth;
    private Instant updatedAt;
}
