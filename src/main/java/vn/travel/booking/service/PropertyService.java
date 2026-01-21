package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.request.property.ReqCreatePropertyDTO;
import vn.travel.booking.dto.request.property.ReqPropertyAmenityDTO;
import vn.travel.booking.dto.request.property.ReqPropertyDecisionDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.property.ResPropertyDTO;
import vn.travel.booking.dto.response.property.ResPropertyDetailDTO;
import vn.travel.booking.dto.response.user.ResUserDTO;
import vn.travel.booking.entity.*;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.mapper.PropertyMapper;
import vn.travel.booking.repository.AmenityRepository;
import vn.travel.booking.repository.NotificationRepository;
import vn.travel.booking.repository.PropertyRepository;
import vn.travel.booking.repository.PropertyTypeRepository;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.PropertyStatus;
import vn.travel.booking.util.error.BusinessException;
import vn.travel.booking.util.error.IdInvalidException;
import vn.travel.booking.util.error.ImageException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserService userService;
    private final PropertyTypeRepository propertyTypeRepository;
    private final PropertyMapper propertyMapper;
    private final AmenityRepository amenityRepository;
    private final NotificationService notificationService;
    private final PaginationMapper paginationMapper;

    public PropertyService(
            PropertyRepository propertyRepository,
            UserService userService,
            PropertyTypeRepository propertyTypeRepository,
            PropertyMapper propertyMapper,
            AmenityRepository amenityRepository,
            NotificationService notificationService,
            PaginationMapper paginationMapper) {
        this.propertyRepository = propertyRepository;
        this.userService = userService;
        this.propertyTypeRepository = propertyTypeRepository;
        this.propertyMapper = propertyMapper;
        this.amenityRepository = amenityRepository;
        this.notificationService = notificationService;
        this.paginationMapper = paginationMapper;
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

        checkOwnership(property);

        List<Amenity> amenities = this.amenityRepository.findByIdIn(req.getAmenityIds());
        property.setAmenities(amenities);

    }

    @Transactional
    public ResPropertyDetailDTO submitForApproval(Long id) {
        Property property = fetchPropertyById(id);

        checkOwnership(property);

        if (property.getStatus() != PropertyStatus.DRAFT &&
                property.getStatus() != PropertyStatus.REJECTED) {
            throw new BusinessException("Property không thể submit ở trạng thái hiện tại");
        }

        if (property.getImages().isEmpty()) {
            throw new ImageException("Property phải có ít nhất 1 ảnh");
        }
        property.setStatus(PropertyStatus.PENDING);

        // notify to admin
        String title = "Property đang chờ kiểm duyệt từ bạn";
        String content = "Vào thư mục kiểm duyệt property để chấp nhận hoặc từ chối property: " + property.getTitle();

        this.notificationService.notifyAdmins(title, content);

        return this.propertyMapper.convertToResPropertyDetailDTO(property);
    }

    @Transactional
    public void decideProperty(Long propertyId, ReqPropertyDecisionDTO req) {

        Property property = fetchPropertyById(propertyId);

        if (property.getStatus() != PropertyStatus.PENDING && property.getStatus() != PropertyStatus.APPROVED) {
            throw new BusinessException("Property không ở trạng thái chờ quyết định");
        }

        Notification noti = new Notification();

        switch (req.getDecision()) {

            case APPROVED -> {
                property.setStatus(PropertyStatus.APPROVED);
                notificationService.notifyUser("Property được duyệt",
                        "Property của bạn đã được admin duyệt",
                        property.getHost());
            }

            case REJECTED -> {
                if (req.getReason() == null || req.getReason().isBlank()) {
                    throw new BusinessException("Phải có lý do khi từ chối");
                }
                property.setStatus(PropertyStatus.REJECTED);
                notificationService.notifyUser("Property bị từ chối",
                        req.getReason(),
                        property.getHost());
            }

            case DRAFT -> { // allow revision
                property.setStatus(PropertyStatus.DRAFT);
                notificationService.notifyUser("Property được chỉnh sửa",
                        "Admin cho phép bạn chỉnh sửa property",
                        property.getHost());
            }

            default -> throw new BusinessException("Decision không hợp lệ");
        }
    }

    public ResPropertyDetailDTO viewPropertyById(long propertyId) {
        Property property = fetchPropertyById(propertyId);
        return this.propertyMapper.convertToResPropertyDetailDTO(property);
    }

    private void checkOwnership(Property property) {
        User current = getCurrentUser();
        if (property.getHost().getId() != current.getId()) {
            throw new AccessDeniedException("Bạn không phải chủ property này");
        }
    }

    public ResultPaginationDTO handleListProperty(Specification spec, Pageable pageable) {
        Page<Property> pageProperty = this.propertyRepository.findAll(spec, pageable);
        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        int totalPages = pageProperty.getTotalPages();
        long totalElements = pageProperty.getTotalElements();

        List<ResPropertyDTO> listProperty = pageProperty.getContent().stream()
                .map(item -> this.propertyMapper.convertToResPropertyDTO(item))
                .collect(Collectors.toList());

        ResultPaginationDTO res = this.paginationMapper.convertToResultPaginationDTO(pageNumber, pageSize, totalPages, totalElements, listProperty);

        return res;
    }


}
