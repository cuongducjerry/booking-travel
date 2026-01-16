package vn.travel.booking.dto.response;

import lombok.*;
import vn.travel.booking.dto.request.ReqCreateUserDTO;

@Data
public class ResUserDTO {
    private long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private int age;

    private Role role;

    @Data
    public static class Role {
        private long id;
        private String name;
    }

}
