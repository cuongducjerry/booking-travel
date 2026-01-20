package vn.travel.booking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.request.property.ReqCreatePropertyDTO;
import vn.travel.booking.dto.request.property.ReqPropertyAmenityDTO;
import vn.travel.booking.dto.response.property.ResPropertyDTO;
import vn.travel.booking.dto.response.property.ResPropertyDetailDTO;
import vn.travel.booking.entity.Amenity;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.PropertyType;
import vn.travel.booking.entity.User;
import vn.travel.booking.mapper.PropertyMapper;
import vn.travel.booking.repository.AmenityRepository;
import vn.travel.booking.repository.PropertyRepository;
import vn.travel.booking.repository.PropertyTypeRepository;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.PropertyStatus;
import vn.travel.booking.util.error.IdInvalidException;
import vn.travel.booking.util.error.ImageException;

import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserService userService;
    private final PropertyTypeRepository propertyTypeRepository;
    private final PropertyMapper propertyMapper;
    private final AmenityRepository amenityRepository;

    public PropertyService(
            PropertyRepository propertyRepository,
            UserService userService,
            PropertyTypeRepository propertyTypeRepository,
            PropertyMapper propertyMapper,
            AmenityRepository amenityRepository) {
        this.propertyRepository = propertyRepository;
        this.userService = userService;
        this.propertyTypeRepository = propertyTypeRepository;
        this.propertyMapper = propertyMapper;
        this.amenityRepository = amenityRepository;
    }

    public User getCurrentUser() {
        long id = SecurityUtil.getCurrentUserId();
        return this.userService.fetchUserById(id);
    }

    public Property fetchPropertyById(long id){
        return this.propertyRepository.findById(id)
                .orElseThrow(() ->
                        new IdInvalidException("Property với id = " + id + " không tồn tại"));
    }

    @Transactional
    public ResPropertyDTO handleCreateProperty(ReqCreatePropertyDTO req) {

        User host = getCurrentUser();

        PropertyType type = this.propertyTypeRepository.findById(req.getPropertyTypeId())
                .orElseThrow(() -> new IdInvalidException("PropertyType không tồn tại"));

        Property property = new Property();
        property.setTitle(req.getTitle());
        property.setDescription(req.getDescription());
        property.setAddress(req.getAddress());
        property.setCity(req.getCity());
        property.setPricePerNight(req.getPricePerNight());
        property.setMaxGuests(req.getMaxGuests());
        property.setCurrency(req.getCurrency());

        property.setPropertyType(type);
        property.setHost(host);

        property.setStatus(PropertyStatus.DRAFT); // important

        propertyRepository.save(property);

        return this.propertyMapper.convertToResPropertyDTO(property);
    }

    @Transactional
    public void handleUpdateAmenities(Long propertyId, ReqPropertyAmenityDTO req) {

        Property property = fetchPropertyById(propertyId);

        List<Amenity> amenities = this.amenityRepository.findByIdIn(req.getAmenityIds());
        property.setAmenities(amenities);

    }

    @Transactional
    public ResPropertyDetailDTO submitForApproval(Long id) {
        Property property = fetchPropertyById(id);

        if (property.getImages().isEmpty()) {
            throw new ImageException("Property phải có ít nhất 1 ảnh");
        }
        property.setStatus(PropertyStatus.PENDING);

        return this.propertyMapper.convertToResPropertyDetailDTO(property);
    }


}
