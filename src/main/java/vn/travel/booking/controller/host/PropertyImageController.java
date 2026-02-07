package vn.travel.booking.controller.host;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.dto.response.propertyimage.ResPropertyImage;
import vn.travel.booking.service.PropertyImageService;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.error.BusinessException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/host")
public class PropertyImageController {

    private final PropertyImageService propertyImageService;

    public PropertyImageController(PropertyImageService propertyImageService) {
        this.propertyImageService = propertyImageService;
    }

    @PostMapping("/properties/{id}/images")
    @PreAuthorize("hasAuthority('PROPERTY_UPLOAD_IMAGE')")
    @ApiMessage("Upload images for property")
    public ResponseEntity<List<ResPropertyImage>> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            throw new BusinessException("Danh sách ảnh trống");
        }

        return ResponseEntity.ok(
                propertyImageService.uploadImages(id, files)
        );
    }

    /* ===========================
       MARK IMAGE FOR DELETE
       =========================== */
    @DeleteMapping("/properties/{propertyId}/images/{imageId}")
    @PreAuthorize("hasAuthority('PROPERTY_DELETE_IMAGE')")
    @ApiMessage("Delete property image")
    public ResponseEntity<Void> markImageForDelete(
            @PathVariable Long propertyId,
            @PathVariable Long imageId) {

        propertyImageService.deleteRealImage(propertyId, imageId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/properties/{propertyId}/image-drafts/{imageId}")
    @PreAuthorize("hasAuthority('PROPERTY_DELETE_IMAGE')")
    @ApiMessage("Delete property image draft ")
    public ResponseEntity<Void> markImageDraftForDelete(
            @PathVariable Long propertyId,
            @PathVariable Long imageId) {

        propertyImageService.deleteDraftImage(propertyId, imageId);
        return ResponseEntity.ok().build();
    }

}
