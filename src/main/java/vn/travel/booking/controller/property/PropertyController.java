package vn.travel.booking.controller.property;

import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.property.ResPropertyDetailDTO;
import vn.travel.booking.entity.Property;
import vn.travel.booking.service.PropertyService;
import vn.travel.booking.specification.PropertySpecification;
import vn.travel.booking.util.annotation.ApiMessage;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // admin, host
    @GetMapping("/properties")
    @PreAuthorize("hasAnyAuthority('PROPERTY_LIST_ALL','PROPERTY_LIST_OWN')")
    @ApiMessage("Fetch all properties")
    public ResponseEntity<ResultPaginationDTO> getAllProperty(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String propertyType,
            Pageable pageable
    ) {

        Specification<Property> spec = Specification
                .where(PropertySpecification.hasTitle(title))
                .and(PropertySpecification.hasStatus(status))
                .and(PropertySpecification.hasPropertyType(propertyType))
                .and(PropertySpecification.filterByCurrentUserRole());

        ResultPaginationDTO res = propertyService.handleListProperty(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/properties/{id}")
    @ApiMessage("Fetch property by id")
    public ResponseEntity<ResPropertyDetailDTO> getPropertyById(@PathVariable Long id) {
        ResPropertyDetailDTO res = this.propertyService.viewPropertyById(id);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/properties/home")
    @ApiMessage("Fetch public properties for home page")
    public ResponseEntity<ResultPaginationDTO> getHomeProperties(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkIn,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOut,

            Pageable pageable
    ) {

        Specification<Property> spec = Specification
                .where(PropertySpecification.isPublicProperty())
                .and(PropertySpecification.hasAddress(address))
                .and(PropertySpecification.hasPropertyType(propertyType))
                .and(PropertySpecification.hasGuestCapacity(guests))
                .and(PropertySpecification.availableBetween(checkIn, checkOut));

        return ResponseEntity.ok(
                propertyService.handleListProperty(spec, pageable)
        );
    }

}

