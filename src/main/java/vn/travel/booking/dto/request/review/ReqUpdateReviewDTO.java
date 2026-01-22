package vn.travel.booking.dto.request.review;

import lombok.Data;

@Data
public class ReqUpdateReviewDTO {
    private long id;
    private int rating;
    private String comment;
}
