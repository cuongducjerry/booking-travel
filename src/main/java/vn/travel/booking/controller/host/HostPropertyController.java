package vn.travel.booking.controller.host;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.request.property.ReqCreatePropertyDTO;
import vn.travel.booking.dto.request.property.ReqPropertyAmenityDTO;
import vn.travel.booking.dto.request.property.ReqUpdatePropertyDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.property.ResPropertyDTO;
import vn.travel.booking.dto.response.property.ResPropertyDetailDTO;
import vn.travel.booking.entity.Property;
import vn.travel.booking.service.PropertyService;
import vn.travel.booking.specification.PropertySpecification;
import vn.travel.booking.util.annotation.ApiMessage;

import java.util.List;

@RestController
@RequestMapping("/api/v1/host")
public class HostPropertyController {

    private final PropertyService propertyService;

    public HostPropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping("/properties")
    @PreAuthorize("hasAuthority('PROPERTY_CREATE')")
    @ApiMessage("Create property (text only)")
    public ResponseEntity<ResPropertyDTO> createProperty(
            @Valid @RequestBody ReqCreatePropertyDTO req) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.propertyService.handleCreateProperty(req));
    }

    @PutMapping("/properties/{id}/amenities")
    @PreAuthorize("hasAuthority('PROPERTY_UPDATE_AMENITY')")
    @ApiMessage("Update property amenities")
    public ResponseEntity<Void> updateAmenities(
            @PathVariable Long id,
            @Valid @RequestBody ReqPropertyAmenityDTO req) {

        this.propertyService.handleUpdateAmenities(id, req);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/properties/{id}/submit")
    @PreAuthorize("hasAuthority('PROPERTY_SUBMIT')")
    @ApiMessage("Submit property for approval")
    public ResponseEntity<ResPropertyDetailDTO> submit(@PathVariable Long id) {
        ResPropertyDetailDTO res = this.propertyService.submitForApproval(id);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/properties/{id}")
    @PreAuthorize("hasAuthority('PROPERTY_UPDATE')")
    @ApiMessage("Update property information")
    public ResponseEntity<ResPropertyDTO> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody ReqUpdatePropertyDTO req) {

        ResPropertyDTO res = propertyService.updateProperty(id, req);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/properties/{id}")
    @PreAuthorize("hasAuthority('PROPERTY_DELETE')")
    @ApiMessage("Host delete property")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        this.propertyService.hostDeleteProperty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/properties/inactive")
    @PreAuthorize("hasAuthority('PROPERTY_LIST_OWN_INACTIVE')")
    @ApiMessage("Fetch inactive properties (no active contract)")
    public ResponseEntity<List<ResPropertyDTO>> getInactiveProperty() {
        List<ResPropertyDTO> res = propertyService.handleListInactiveProperty();
        return ResponseEntity.ok(res);
    }

}
