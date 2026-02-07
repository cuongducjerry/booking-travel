package vn.travel.booking.controller.host;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.response.propertyimage.ResPropertyImage;
import vn.travel.booking.service.PropertyImageDraftService;
import vn.travel.booking.util.annotation.ApiMessage;

import java.util.List;

@RestController
@RequestMapping("/api/v1/host")
public class PropertyImageDraftController {

    private final PropertyImageDraftService propertyImageDraftService;

    public PropertyImageDraftController(PropertyImageDraftService propertyImageDraftService) {
        this.propertyImageDraftService = propertyImageDraftService;
    }

    @GetMapping("/image-drafts/{propertyId}")
    @PreAuthorize("hasAuthority('PROPERTY_DRAFT_IMAGE_LIST')")
    @ApiMessage("Get list image draft by property id")
    public ResponseEntity<List<ResPropertyImage>> listPropertyDraftImages(@PathVariable Long propertyId) {
        return ResponseEntity.status(HttpStatus.OK).body(propertyImageDraftService.handleListPropertyDraftImage(propertyId));
    }

//    @DeleteMapping("/image-drafts/{id}")
//    @PreAuthorize("hasAuthority('DELETE_PROPERTY_DRAFT_IMAGE')")
//    @ApiMessage("Delete image draft by id")
//    public ResponseEntity<Void> deleteImagDraft(@PathVariable Long id) {
//        propertyImageDraftService.handleDeleteDraftImage(id);
//        return ResponseEntity.status(HttpStatus.OK).body(null);
//    }

}
