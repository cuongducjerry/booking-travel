package vn.travel.booking.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.PropertyImage;
import vn.travel.booking.entity.User;
import vn.travel.booking.repository.PropertyImageRepository;
import vn.travel.booking.repository.PropertyRepository;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.PropertyStatus;
import vn.travel.booking.util.error.IdInvalidException;
import vn.travel.booking.util.error.ImageException;

import java.util.List;

@Service
public class PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;
    private final PropertyRepository propertyRepository;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    public PropertyImageService(
            PropertyImageRepository propertyImageRepository,
            PropertyRepository propertyRepository,
            CloudinaryService cloudinaryService,
            UserService userService) {
        this.propertyImageRepository = propertyImageRepository;
        this.propertyRepository = propertyRepository;
        this.cloudinaryService = cloudinaryService;
        this.userService = userService;
    }

    @Transactional
    public void uploadImages(Long propertyId, List<MultipartFile> files) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IdInvalidException("Property không tồn tại"));

        User current = this.userService.fetchUserById(SecurityUtil.getCurrentUserId());

        if (property.getHost().getId() != (current.getId())) {
            throw new AccessDeniedException("Không phải property của bạn");
        }

        for (MultipartFile file : files) {
            String url = cloudinaryService.uploadPropertyImage(file, propertyId);

            PropertyImage image = new PropertyImage();
            image.setImageUrl(url);
            image.setProperty(property);

            this.propertyImageRepository.save(image);
        }
    }

}
