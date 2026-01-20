package vn.travel.booking.controller.host;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.service.PropertyImageService;
import vn.travel.booking.util.annotation.ApiMessage;

import java.util.List;

@RestController
@RequestMapping("/api/v1/host")
public class HostPropertyImageController {

    private final PropertyImageService propertyImageService;

    public HostPropertyImageController(PropertyImageService propertyImageService) {
        this.propertyImageService = propertyImageService;
    }

    @PostMapping("/properties/{id}/images")
    @PreAuthorize("hasAuthority('PROPERTY_UPLOAD_IMAGE')")
    @ApiMessage("Upload images for property")
    public ResponseEntity<Void> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {

        this.propertyImageService.uploadImages(id, files);
        return ResponseEntity.ok().build();
    }

}
