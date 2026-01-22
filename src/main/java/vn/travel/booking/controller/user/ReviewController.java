package vn.travel.booking.controller.user;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.request.review.ReqCreateReviewDTO;
import vn.travel.booking.dto.request.review.ReqUpdateReviewDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.review.ResReviewDTO;
import vn.travel.booking.service.ReviewService;
import vn.travel.booking.util.annotation.ApiMessage;


@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{propertyId}")
    @PreAuthorize("hasAuthority('REVIEW_CREATE')")
    @ApiMessage("Create a new review")
    public ResponseEntity<ResReviewDTO> create(
            @PathVariable Long propertyId,
            @RequestBody ReqCreateReviewDTO req
    ) {
        ResReviewDTO dto = reviewService.create(propertyId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('REVIEW_UPDATE')")
    @ApiMessage("Update a review by id")
    public ResponseEntity<ResReviewDTO> update(@RequestBody ReqUpdateReviewDTO req) {
        ResReviewDTO dto = reviewService.update(req);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAuthority('REVIEW_DELETE')")
    @ApiMessage("Delete a review by id")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/property/{propertyId}")
    @PreAuthorize("hasAuthority('REVIEW_LIST_IN_PROPERTY')")
    @ApiMessage("List a review by property id")
    public ResultPaginationDTO getByProperty(
            @PathVariable Long propertyId,
            Pageable pageable
    ) {
        return reviewService.getByProperty(propertyId, pageable);
    }

}
