package vn.travel.booking.dto.request.review;

import lombok.Data;

@Data
public class ReqCreateReviewDTO {
    private int rating;
    private String comment;
}
