package vn.travel.booking.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.util.error.BusinessException;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 4 * 1024 * 1024;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // ===================== AVATAR =====================
    public String uploadAvatar(MultipartFile file) {
        return uploadImage(file, "avatars/users");
    }

    // ===================== PROPERTY IMAGE =====================
    public String uploadPropertyImage(MultipartFile file, Long propertyId) {
        String folder = "properties/" + propertyId;
        return uploadImage(file, folder);
    }

    // ===================== COMMON =====================
    private String uploadImage(MultipartFile file, String folder) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File ảnh không được để trống");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Chỉ cho phép upload ảnh JPG, PNG, WEBP");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Ảnh tối đa 4MB");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", folder,
                            "resource_type", "image"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload ảnh thất bại", e);
        }
    }

    /**
     * Delete photos by URL
     */
    public void delete(String imageUrl) {

        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, Map.of());

        } catch (Exception e) {
            throw new BusinessException("Delete image failed");
        }
    }

    /**
     * Get public_id from Cloudinary URL
     * VD:
     * https://res.cloudinary.com/xxx/image/upload/v123/properties/1/abc.jpg
     * -> properties/1/abc
     */
    private String extractPublicId(String imageUrl) {

        String noExt = imageUrl.substring(0, imageUrl.lastIndexOf("."));
        return noExt.substring(noExt.indexOf("properties"));
    }

}
