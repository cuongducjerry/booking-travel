package vn.travel.booking.dto.response.review;

import lombok.Data;

import java.time.Instant;

@Data
public class ResReviewDTO {
    private Long id;
    private int rating;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;
    private String imageUrl;
    private String commentUserName;
}
