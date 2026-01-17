package vn.travel.booking.dto.response.user;

import lombok.*;

import java.time.Instant;

@Data
public class ResUserDTO {
    private long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private int age;

    private Role role;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    public static class Role {
        private long id;
        private String name;
    }

}
