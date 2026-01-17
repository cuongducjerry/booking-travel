package vn.travel.booking.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ResUpdatePasswordDTO {
    private String message;
    private Instant updatedAt;
}
