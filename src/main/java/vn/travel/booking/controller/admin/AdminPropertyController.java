package vn.travel.booking.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.request.property.ReqPropertyDecisionDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.entity.Property;
import vn.travel.booking.service.PropertyService;
import vn.travel.booking.specification.PropertySpecification;
import vn.travel.booking.specification.UserSpecification;
import vn.travel.booking.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminPropertyController {

    private final PropertyService propertyService;

    public AdminPropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PutMapping("/properties/{id}/decision")
    @PreAuthorize("hasAuthority('PROPERTY_APPROVE')")
    @ApiMessage("Admin decision for property")
    public ResponseEntity<Void> decide(
            @PathVariable Long id,
            @RequestBody ReqPropertyDecisionDTO req) {

        this.propertyService.decideProperty(id, req);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/properties/{id}/approve-delete")
    @PreAuthorize("hasAuthority('PROPERTY_DELETE_APPROVE')")
    @ApiMessage("Admin approve delete property")
    public ResponseEntity<Void> approveDelete(@PathVariable Long id) {
        this.propertyService.adminApproveDelete(id);
        return ResponseEntity.ok().build();
    }

}
