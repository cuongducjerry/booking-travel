package vn.travel.booking.controller.user;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.service.WishlistService;
import vn.travel.booking.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/toggle/{propertyId}")
    @PreAuthorize("hasAuthority('WISHLIST_TOGGLE')")
    @ApiMessage("Toggle wishlist")
    public ResponseEntity<Void> toggleWishlist(@PathVariable Long propertyId) {
        this.wishlistService.toggleWishlist(propertyId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/my-wishlist")
    @PreAuthorize("hasAuthority('WISHLIST_PERSONAL')")
    @ApiMessage("Wishlist personal")
    public ResponseEntity<ResultPaginationDTO> myWishlist(Pageable page) {
        return ResponseEntity.status(HttpStatus.OK).body(this.wishlistService.getMyWishlist(page));
    }

    @DeleteMapping("/{propertyId}")
    @PreAuthorize("hasAuthority('WISHLIST_DELETE')")
    @ApiMessage("Delete a wishlist")
    public ResponseEntity<Void> removeWishlist(@PathVariable Long propertyId) {
        wishlistService.remove(propertyId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
