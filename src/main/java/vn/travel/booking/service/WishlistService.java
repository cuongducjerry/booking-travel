package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.property.ResPropertyWishlistDTO;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.User;
import vn.travel.booking.entity.Wishlist;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.mapper.PropertyMapper;
import vn.travel.booking.repository.PropertyRepository;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.repository.WishlistRepository;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.error.IdInvalidException;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;
    private final PaginationMapper paginationMapper;

    public WishlistService(
            WishlistRepository wishlistRepository,
            UserRepository userRepository,
            PropertyRepository propertyRepository,
            PropertyMapper propertyMapper,
            PaginationMapper paginationMapper) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
        this.paginationMapper = paginationMapper;
    }

    /* ================== PUBLIC API ================== */

    @Transactional
    public void toggleWishlist(long propertyId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Wishlist wishlist = findWishlist(userId, propertyId);

        if (wishlist != null) {
            wishlist.setActive(!wishlist.isActive());
            return;
        }

        Wishlist newWishlist = buildWishlist(userId, propertyId);
        wishlistRepository.save(newWishlist);
    }

    public ResultPaginationDTO getMyWishlist(Pageable pageable) {

        Long userId = SecurityUtil.getCurrentUserId();

        Page<Wishlist> pageWishlist =
                wishlistRepository.findByUser_IdAndActiveTrue(userId, pageable);

        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        int totalPages = pageWishlist.getTotalPages();
        long totalElements = pageWishlist.getTotalElements();

        List<ResPropertyWishlistDTO> properties = pageWishlist.getContent()
                .stream()
                .map(w -> this.propertyMapper.convertToResPropertyWishlistDTO(w.getProperty()))
                .toList();

        return paginationMapper.convertToResultPaginationDTO(
                pageNumber,
                pageSize,
                totalPages,
                totalElements,
                properties
        );
    }


    @Transactional
    public void remove(long propertyId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Wishlist wishlist = findWishlistOrThrow(userId, propertyId);
        wishlist.setActive(false);
    }

    /* ================== PRIVATE METHODS ================== */

    private Wishlist findWishlist(Long userId, Long propertyId) {
        return wishlistRepository
                .findByUser_IdAndProperty_Id(userId, propertyId)
                .orElse(null);
    }

    private Wishlist findWishlistOrThrow(Long userId, Long propertyId) {
        return wishlistRepository
                .findByUser_IdAndProperty_Id(userId, propertyId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));
    }

    private Wishlist buildWishlist(Long userId, Long propertyId) {
        User user = getUser(userId);
        Property property = getProperty(propertyId);

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProperty(property);
        wishlist.setActive(true);
        return wishlist;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found"));
    }

    private Property getProperty(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

}
