package vn.travel.booking.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqCreateReviewDTO {

    @Min(value = 1, message = "rating tối thiểu là 1")
    @Max(value = 5, message = "rating tối đa là 5")
    private int rating;

    @Size(max = 1000, message = "comment tối đa 1000 ký tự")
    private String comment;
}

