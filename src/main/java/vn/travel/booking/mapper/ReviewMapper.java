package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.review.ResReviewDTO;
import vn.travel.booking.entity.Review;

@Component
public class ReviewMapper {

    public ResReviewDTO convertToResReviewDTO(Review review) {
        ResReviewDTO resReviewDTO = new ResReviewDTO();
        resReviewDTO.setId(review.getId());
        resReviewDTO.setRating(review.getRating());
        resReviewDTO.setComment(review.getComment());
        resReviewDTO.setCreatedAt(review.getCreatedAt());
        resReviewDTO.setUpdatedAt(review.getUpdatedAt());
        resReviewDTO.setImageUrl(review.getUser().getAvatarUrl());
        resReviewDTO.setCommentUserName(review.getUser().getFullName());
        return resReviewDTO;
    }

}
