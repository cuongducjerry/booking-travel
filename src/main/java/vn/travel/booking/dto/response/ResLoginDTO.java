package vn.travel.booking.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import vn.travel.booking.entity.Role;

import java.util.List;

@Getter
@Setter
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonIgnore
    private String refreshToken;

    private UserLogin user;

    private List<String> permissions;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin {
        private long id;
        private String email;
        private String fullName;
        private String role;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsideToken {
        private long id;
        private String email;
        private String fullName;
    }

}
