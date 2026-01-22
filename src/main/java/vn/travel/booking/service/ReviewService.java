package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.request.review.ReqCreateReviewDTO;
import vn.travel.booking.dto.request.review.ReqUpdateReviewDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.review.ResReviewDTO;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.Review;
import vn.travel.booking.entity.User;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.mapper.ReviewMapper;
import vn.travel.booking.repository.PropertyRepository;
import vn.travel.booking.repository.ReviewRepository;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.error.IdInvalidException;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PaginationMapper paginationMapper;

    public ReviewService(
            ReviewRepository reviewRepository,
            ReviewMapper reviewMapper,
            UserRepository userRepository,
            PropertyRepository propertyRepository,
            PaginationMapper paginationMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.paginationMapper = paginationMapper;
    }

    /* ================= CREATE ================= */

    @Transactional
    public ResReviewDTO create(Long propertyId, ReqCreateReviewDTO req) {

        Long userId = SecurityUtil.getCurrentUserId();

        // No duplicate reviews allowed
        reviewRepository.findByUser_IdAndProperty_Id(userId, propertyId)
                .ifPresent(r -> {
                    throw new RuntimeException("Bạn đã đánh giá property này, có thể chỉnh sửa hoặc xóa nếu muốn!");
                });

        Review review = new Review();
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setUser(getUser(userId));
        review.setProperty(getProperty(propertyId));

        reviewRepository.save(review);

        return this.reviewMapper.convertToResReviewDTO(review);
    }

    /* ================= UPDATE ================= */

    @Transactional
    public ResReviewDTO update(ReqUpdateReviewDTO req) {

        Long userId = SecurityUtil.getCurrentUserId();

        Review review = reviewRepository.findById(req.getId())
                .orElseThrow(() -> new IdInvalidException("Review với id = " + req.getId() + " không tồn tại!"));

        if (review.getUser().getId() != userId) {
            throw new RuntimeException("Bạn không có quyền sửa đánh giá của người khác!");
        }

        review.setRating(req.getRating());
        review.setComment(req.getComment());

        return this.reviewMapper.convertToResReviewDTO(review);
    }

    /* ================= DELETE (SOFT) ================= */

    @Transactional
    public void delete(Long reviewId) {

        Long userId = SecurityUtil.getCurrentUserId();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IdInvalidException("Review với id = " + reviewId + " không tồn tại!"));

        if (review.getUser().getId() != userId) {
            throw new RuntimeException("Bạn không có quyền xóa đánh giá của người khác!");
        }

        // 🔥 GỌI DELETE NHƯNG LÀ SOFT DELETE
        reviewRepository.delete(review);
    }

    /* ================= READ ================= */

    public ResultPaginationDTO getByProperty(Long propertyId, Pageable pageable) {
        Page<Review> pageReview = reviewRepository.findByProperty_Id(propertyId, pageable);
        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        int totalPages = pageReview.getTotalPages();
        long totalElements = pageReview.getTotalElements();

        List<ResReviewDTO> reviews = pageReview.getContent()
                .stream()
                .map(item -> this.reviewMapper.convertToResReviewDTO(item))
                .toList();

        return paginationMapper.convertToResultPaginationDTO(
                pageNumber,
                pageSize,
                totalPages,
                totalElements,
                reviews
        );

    }

    /* ================= PRIVATE ================= */

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User với id = " + userId + " không tồn tại!"));
    }

    private Property getProperty(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IdInvalidException("Property với id = " + propertyId + " không tồn tại!"));
    }

}
