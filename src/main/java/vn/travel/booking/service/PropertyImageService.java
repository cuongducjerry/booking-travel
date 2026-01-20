package vn.travel.booking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.PropertyImage;
import vn.travel.booking.repository.PropertyImageRepository;
import vn.travel.booking.repository.PropertyRepository;
import vn.travel.booking.util.error.IdInvalidException;

import java.util.List;

@Service
public class PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;
    private final PropertyRepository propertyRepository;
    private final CloudinaryService cloudinaryService;

    public PropertyImageService(
            PropertyImageRepository propertyImageRepository,
            PropertyRepository propertyRepository,
            CloudinaryService cloudinaryService) {
        this.propertyImageRepository = propertyImageRepository;
        this.propertyRepository = propertyRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public void uploadImages(Long propertyId, List<MultipartFile> files) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IdInvalidException("Property không tồn tại"));

        for (MultipartFile file : files) {
            String url = cloudinaryService.uploadPropertyImage(file, propertyId);

            PropertyImage image = new PropertyImage();
            image.setImageUrl(url);
            image.setProperty(property);

            this.propertyImageRepository.save(image);
        }
    }

}
