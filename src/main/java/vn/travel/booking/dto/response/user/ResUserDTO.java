package vn.travel.booking.dto.response.user;

import lombok.*;
import vn.travel.booking.util.constant.StatusUser;

import java.time.Instant;

@Data
public class ResUserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private int age;
    private StatusUser status;

    private Role role;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @Data
    public static class Role {
        private long id;
        private String name;
    }

}
