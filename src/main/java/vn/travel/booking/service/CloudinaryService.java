package vn.travel.booking.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // chỉ cho phép các mime type ảnh
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    // giới hạn dung lượng (4MB)
    private static final long MAX_FILE_SIZE = 4 * 1024 * 1024;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadAvatar(MultipartFile file) {

        // 1️⃣ check file rỗng
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File ảnh không được để trống");
        }

        // 2️⃣ check content-type
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Chỉ cho phép upload ảnh JPG, PNG, WEBP");
        }

        // 3️⃣ check dung lượng
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Ảnh tối đa 4MB");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", "avatars_user",
                            "resource_type", "image"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload avatar thất bại", e);
        }
    }
}
