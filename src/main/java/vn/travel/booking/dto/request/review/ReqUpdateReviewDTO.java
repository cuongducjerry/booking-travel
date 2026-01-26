package vn.travel.booking.dto.request.review;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReqUpdateReviewDTO {

    @NotNull(message = "id không được để trống")
    @Positive(message = "id không hợp lệ")
    private Long id;

    @Min(value = 1, message = "rating tối thiểu là 1")
    @Max(value = 5, message = "rating tối đa là 5")
    private Integer rating;

    @Size(max = 1000, message = "comment tối đa 1000 ký tự")
    private String comment;
}

