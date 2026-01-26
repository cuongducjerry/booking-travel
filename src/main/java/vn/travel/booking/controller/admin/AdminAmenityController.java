package vn.travel.booking.controller.admin;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.request.amenity.ReqCreateAmenityDTO;
import vn.travel.booking.dto.request.amenity.ReqUpdateAmenityDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.amenity.ResAmenityDTO;
import vn.travel.booking.entity.Amenity;
import vn.travel.booking.service.AmenityService;
import vn.travel.booking.specification.AmenitySpecification;
import vn.travel.booking.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminAmenityController {

    private final AmenityService amenityService;

    public AdminAmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    @PostMapping("/amenities")
    @PreAuthorize("hasAuthority('AMENITY_CREATE')")
    @ApiMessage("Create a new amenity")
    public ResponseEntity<ResAmenityDTO> register(@Valid @RequestBody ReqCreateAmenityDTO req) {
        ResAmenityDTO res = this.amenityService.handleCreateAmenity(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/amenities")
    @PreAuthorize("hasAuthority('AMENITY_UPDATE')")
    @ApiMessage("Update a amenity")
    public ResponseEntity<ResAmenityDTO> updateAmenity(@Valid @RequestBody ReqUpdateAmenityDTO req) {
        ResAmenityDTO res = this.amenityService.handleUpdateAmenity(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
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

    @GetMapping("/amenities/{id}")
    @PreAuthorize("hasAuthority('AMENITY_VIEW')")
    @ApiMessage("Fetch amenity by id")
    public ResponseEntity<ResAmenityDTO> getAmenityById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.amenityService.viewAmenityById(id));
    }

    @DeleteMapping("/amenities/{id}")
    @PreAuthorize("hasAuthority('AMENITY_DELETE')")
    @ApiMessage("Delete a amenity")
    public ResponseEntity<Void> deleteAmenity(@PathVariable Long id) {
        this.amenityService.handleDeleteAmenity(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
