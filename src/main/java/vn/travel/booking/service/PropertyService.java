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
import vn.travel.booking.dto.request.property.ReqUpdatePropertyDTO;
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
import vn.travel.booking.service.notification.NotificationService;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.NotificationType;
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
    private final PaginationMapper paginationMapper;
    private final PropertyImageService propertyImageService;
    private final NotificationService notificationService;

    public PropertyService(
            PropertyRepository propertyRepository,
            UserService userService,
            PropertyTypeRepository propertyTypeRepository,
            PropertyMapper propertyMapper,
            AmenityRepository amenityRepository,
            PaginationMapper paginationMapper,
            PropertyImageService propertyImageService,
            NotificationService notificationService) {
        this.propertyRepository = propertyRepository;
        this.userService = userService;
        this.propertyTypeRepository = propertyTypeRepository;
        this.propertyMapper = propertyMapper;
        this.amenityRepository = amenityRepository;
        this.paginationMapper = paginationMapper;
        this.propertyImageService = propertyImageService;
        this.notificationService = notificationService;
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

    private Property fetchAndCheckOwner(Long propertyId) {

        Property property = fetchPropertyById(propertyId);

        User current = getCurrentUser();

        if (property.getHost().getId() != current.getId()) {
            throw new AccessDeniedException("Bạn không phải chủ property này");
        }

        return property;
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
        notificationService.notifyAdmins(
                NotificationType.PROPERTY,
                "Property #" + property.getId() + " đang chờ kiểm duyệt",
                "Host " + property.getHost().getFullName()
                        + " đã submit property:\n"
                        + "• Tên: " + property.getTitle() + "\n"
                        + "• Địa chỉ: " + property.getAddress(),
                true
        );

        return this.propertyMapper.convertToResPropertyDetailDTO(property);
    }

    @Transactional
    public ResPropertyDetailDTO updateProperty(Long id, ReqUpdatePropertyDTO req) {

        Property p = fetchAndCheckOwner(id);

        // state rule
        switch (p.getStatus()) {
            case PENDING ->
                    throw new BusinessException("Property đang chờ duyệt, không thể sửa");
            case APPROVED ->
                    p.setStatus(PropertyStatus.DRAFT); // edit approved => draft
        }

        if (req.getTitle() != null) p.setTitle(req.getTitle());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getAddress() != null) p.setAddress(req.getAddress());
        if (req.getCity() != null) p.setCity(req.getCity());
        if (req.getCurrency() != null) p.setCurrency(req.getCurrency());
        if (req.getPricePerNight() > 0) p.setPricePerNight(req.getPricePerNight());
        if (req.getMaxGuests() > 0) p.setMaxGuests(req.getMaxGuests());

        if (req.getAmenityIds() != null) {
            p.setAmenities(amenityRepository.findByIdIn(req.getAmenityIds()));
        }

        return propertyMapper.convertToResPropertyDetailDTO(p);
    }

    @Transactional
    public void decideProperty(Long propertyId, ReqPropertyDecisionDTO req) {

        Property property = fetchPropertyById(propertyId);

        // Validate status
        if (property.getStatus() != PropertyStatus.PENDING &&
                property.getStatus() != PropertyStatus.APPROVED) {
            throw new BusinessException("Property không ở trạng thái chờ quyết định");
        }

        Long hostId = property.getHost().getId();

        switch (req.getDecision()) {

            case APPROVED -> {
                property.setStatus(PropertyStatus.APPROVED);

                // APPLY IMAGE DRAFT
                propertyImageService.applyDraft(propertyId);

                // notify HOST
                notificationService.notify(
                        hostId,
                        NotificationType.PROPERTY,
                        "Property #" + property.getId() + " đã được duyệt",
                        "Property \"" + property.getTitle()
                                + "\" của bạn đã được admin phê duyệt và sẵn sàng hiển thị.",
                        false
                );
            }

            case REJECTED -> {
                if (req.getReason() == null || req.getReason().isBlank()) {
                    throw new BusinessException("Phải có lý do khi từ chối");
                }

                property.setStatus(PropertyStatus.REJECTED);

                // notify HOST (send email)
                notificationService.notify(
                        hostId,
                        NotificationType.PROPERTY,
                        "Property #" + property.getId() + " bị từ chối",
                        "Property \"" + property.getTitle()
                                + "\" đã bị từ chối.\n\nLý do: "
                                + req.getReason(),
                        true
                );
            }

            case DRAFT -> { // allow revision
                property.setStatus(PropertyStatus.DRAFT);

                // notify HOST
                notificationService.notify(
                        hostId,
                        NotificationType.PROPERTY,
                        "Property #" + property.getId() + " cần chỉnh sửa",
                        "Admin yêu cầu bạn chỉnh sửa property \""
                                + property.getTitle()
                                + "\" trước khi duyệt lại.",
                        false
                );
            }

            default -> throw new BusinessException("Decision không hợp lệ");
        }
    }



    @Transactional
    public void hostDeleteProperty(Long propertyId) {

        Property property = fetchPropertyById(propertyId);
        checkOwnership(property);

        // Booking is available -> cannot be deleted.
        if (!property.getBookings().isEmpty()) {
            throw new BusinessException("Property đã có booking, không thể xóa");
        }

        // CASE 1: DRAFT -> soft delete
        if (property.getStatus() == PropertyStatus.DRAFT) {
            property.setStatus(PropertyStatus.DELETED);
            propertyRepository.delete(property);
            return;
        }

        // CASE 2: APPROVED -> send admin
        if (property.getStatus() == PropertyStatus.APPROVED) {
            property.setStatus(PropertyStatus.DELETE_PENDING);
            return;
        }

        throw new BusinessException("Không thể xóa property ở trạng thái hiện tại");
    }


    @Transactional
    public void adminApproveDelete(Long propertyId) {

        Property property = fetchPropertyById(propertyId);

        if (property.getStatus() != PropertyStatus.DELETE_PENDING) {
            throw new BusinessException("Property không ở trạng thái chờ xóa");
        }

        // soft delete
        propertyRepository.delete(property);
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
