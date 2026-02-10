package vn.travel.booking.dto.request;

import lombok.Data;

@Data
public class SocialLoginReq {
    private String provider; // GOOGLE
    private String email;
    private String name;
    private String avatar;
}
