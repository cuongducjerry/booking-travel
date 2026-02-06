package vn.travel.booking.controller.amenity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.entity.Amenity;
import vn.travel.booking.service.AmenityService;
import vn.travel.booking.specification.AmenitySpecification;
import vn.travel.booking.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class AmenityController {

    private final AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    @GetMapping("/amenities")
    @PreAuthorize("hasAuthority('AMENITY_LIST_ALL')")
    @ApiMessage("Fetch all amenity")
    public ResponseEntity<ResultPaginationDTO> getAllAmenity(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {

        Specification<Amenity> spec = Specification
                .where(AmenitySpecification.keyword(keyword));

        return ResponseEntity.status(HttpStatus.OK).body(this.amenityService.handleListAmenity(spec, pageable));
    }

}
