package vn.travel.booking.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.request.propertytype.ReqCreatePropertyTypeDTO;
import vn.travel.booking.dto.request.propertytype.ReqUpdatePropertyTypeDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.propertytype.ResPropertyTypeDTO;
import vn.travel.booking.entity.PropertyType;
import vn.travel.booking.service.PropertyTypeService;
import vn.travel.booking.specification.PropertyTypeSpecification;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.error.NameInvalidException;

// superadmin
@RestController
@RequestMapping("/api/v1/admin")
public class AdminPropertyTypeController {

    private final PropertyTypeService propertyTypeService;

    public AdminPropertyTypeController(PropertyTypeService propertyTypeService) {
        this.propertyTypeService = propertyTypeService;
    }

    @PostMapping("/property-types")
    @PreAuthorize("hasAuthority('PROPERTY_TYPE_CREATE')")
    @ApiMessage("Create a property type")
    public ResponseEntity<ResPropertyTypeDTO> createPropertyType(@RequestBody ReqCreatePropertyTypeDTO dto) throws NameInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.propertyTypeService.handleCreatePropertyType(dto));
    }

    @PutMapping("/property-types")
    @PreAuthorize("hasAuthority('PROPERTY_TYPE_UPDATE')")
    @ApiMessage("Update a property type")
    public ResponseEntity<ResPropertyTypeDTO> updatePropertyType(@RequestBody ReqUpdatePropertyTypeDTO dto) {
        ResPropertyTypeDTO resPropertyTypeUpdateDTO = this.propertyTypeService.handleUpdatePropertyType(dto);
        return ResponseEntity.status(HttpStatus.OK).body(resPropertyTypeUpdateDTO);
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

    @GetMapping("/property-types/{id}")
    @PreAuthorize("hasAuthority('PROPERTY_TYPE_VIEW')")
    @ApiMessage("Fetch property type by id")
    public ResponseEntity<ResPropertyTypeDTO> getPropertyTypeById(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.propertyTypeService.viewPropertyTypeById(id));
    }

    @DeleteMapping("/property-types/{id}")
    @PreAuthorize("hasAuthority('PROPERTY_TYPE_DELETE')")
    @ApiMessage("Delete a property type")
    public ResponseEntity<Void> deletePropertyType(@PathVariable long id) {
        this.propertyTypeService.handleDeletePropertyType(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
