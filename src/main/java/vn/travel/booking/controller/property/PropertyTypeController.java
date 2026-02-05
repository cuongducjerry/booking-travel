package vn.travel.booking.controller.property;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.propertytype.ResPropertyTypeDTO;
import vn.travel.booking.entity.PropertyType;
import vn.travel.booking.service.PropertyService;
import vn.travel.booking.service.PropertyTypeService;
import vn.travel.booking.specification.PropertyTypeSpecification;
import vn.travel.booking.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class PropertyTypeController {

    private final PropertyTypeService propertyTypeService;

    public PropertyTypeController(PropertyTypeService propertyTypeService) {
        this.propertyTypeService = propertyTypeService;
    }

    @GetMapping("/property-types")
    @PreAuthorize("hasAuthority('PROPERTY_TYPE_LIST_ALL')")
    @ApiMessage("Fetch all property type")
    public ResponseEntity<ResultPaginationDTO> getAllPropertyType(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {

        Specification<PropertyType> spec = Specification
                .where(PropertyTypeSpecification.keyword(keyword));

        return ResponseEntity.status(HttpStatus.OK).body(this.propertyTypeService.handleListPropertyType(spec, pageable));
    }

}
