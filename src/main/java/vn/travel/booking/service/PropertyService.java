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
import vn.travel.booking.entity.*;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.mapper.PropertyMapper;
import vn.travel.booking.repository.*;
import vn.travel.booking.service.notification.NotificationService;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.ContractStatus;
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
    private final PropertyImageRepository propertyImageRepository;
    private final NotificationService notificationService;
    private final PropertyImageDraftRepository propertyImageDraftRepository;
    private final ContractPropertyRepository contractPropertyRepository;
    private final HostContractRepository hostContractRepository;

    public PropertyService(
            PropertyRepository propertyRepository,
            UserService userService,
            PropertyTypeRepository propertyTypeRepository,
            PropertyMapper propertyMapper,
            AmenityRepository amenityRepository,
            PaginationMapper paginationMapper,
            PropertyImageService propertyImageService,
            NotificationService notificationService,
            PropertyImageDraftRepository propertyImageDraftRepository,
            ContractPropertyRepository contractPropertyRepository,
            HostContractRepository hostContractRepository,
            PropertyImageRepository propertyImageRepository) {
        this.propertyRepository = propertyRepository;
        this.userService = userService;
        this.propertyTypeRepository = propertyTypeRepository;
        this.propertyMapper = propertyMapper;
        this.amenityRepository = amenityRepository;
        this.paginationMapper = paginationMapper;
        this.propertyImageService = propertyImageService;
        this.notificationService = notificationService;
        this.propertyImageDraftRepository = propertyImageDraftRepository;
        this.contractPropertyRepository = contractPropertyRepository;
        this.hostContractRepository = hostContractRepository;
        this.propertyImageRepository = propertyImageRepository;
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

        PropertyType type = propertyTypeRepository.findById(req.getPropertyTypeId())
                .orElseThrow(() -> new IdInvalidException("PropertyType không tồn tại"));

        HostContract contract = hostContractRepository.findById(req.getContractId())
                .orElseThrow(() -> new IdInvalidException("Contract không tồn tại"));

        // (It is recommended) to check that the contract belongs to the user.
        if (!contract.getHost().getId().equals(host.getId())) {
            throw new AccessDeniedException("Không có quyền thêm property vào contract này");
        }

        Property property = new Property();
        property.setTitle(req.getTitle());
        property.setDescription(req.getDescription());
        property.setAddress(req.getAddress());
        property.setCity(req.getCity());
        property.setPricePerNight(req.getPricePerNight());
        property.setMaxGuests(req.getMaxGuests());
        property.setCurrency(req.getCurrency());
        property.setLatitude(req.getLatitude());
        property.setLongitude(req.getLongitude());

        property.setPropertyType(type);
        property.setHost(host);
        property.setContract(contract);

        property.setStatus(PropertyStatus.DRAFT);

        contract.setStatus(ContractStatus.PENDING);

        propertyRepository.save(property);

        return propertyMapper.convertToResPropertyDTO(property);
    }

    @Transactional
    public void handleUpdateAmenities(Long propertyId, ReqPropertyAmenityDTO req) {

        Property property = fetchPropertyById(propertyId);

        checkOwnership(property);

        List<Amenity> amenities = this.amenityRepository.findByIdIn(req.getAmenityIds());
        property.setAmenities(amenities);
        propertyRepository.save(property);
    }

    @Transactional
    public ResPropertyDetailDTO submitForApproval(Long id) {
        Property property = fetchPropertyById(id);

        checkOwnership(property);

        if (property.getStatus() != PropertyStatus.DRAFT &&
                property.getStatus() != PropertyStatus.REJECTED) {
            throw new BusinessException("Property không thể submit ở trạng thái hiện tại");
        }

        List<PropertyImageDraft> listImageDraft = propertyImageDraftRepository.findByProperty_Id(id);
        long countImageOld = this.propertyImageRepository.countByProperty_Id(property.getId());

        if (listImageDraft.isEmpty() && countImageOld == 0) {
            throw new ImageException("Property phải có ít nhất 1 ảnh mẫu!");
        }
        property.setStatus(PropertyStatus.PENDING);
        this.propertyRepository.save(property);

        // notify to admin
        notificationService.notifyAdmins(
                NotificationType.PROPERTY,
                "Property #" + property.getId() + " đang chờ kiểm duyệt",
                "Host " + property.getHost().getFullName()
                        + " đã submit property:\n"
                        + "• Tên: " + property.getTitle() + "\n"
                        + "• Địa chỉ: " + property.getAddress(),
                false
        );

        return this.propertyMapper.convertToResPropertyDetailDTO(property);
    }

    @Transactional
    public ResPropertyDTO updateProperty(Long id, ReqUpdatePropertyDTO req) {

        Property p = fetchAndCheckOwner(id);

        // state rule
        switch (p.getStatus()) {
            case PENDING ->
                    throw new BusinessException("Property đang chờ duyệt, không thể sửa");
            case APPROVED ->
                    p.setStatus(PropertyStatus.DRAFT); // edit approved => draft
            case REJECTED ->
                    p.setStatus(PropertyStatus.DRAFT);
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

        if (req.getLatitude() != null) {
            p.setLatitude(req.getLatitude());
        }

        if (req.getLongitude() != null) {
            p.setLongitude(req.getLongitude());
        }


        return propertyMapper.convertToResPropertyDTO(p);
    }

    @Transactional
    public void decideProperty(Long propertyId, ReqPropertyDecisionDTO req) {

        Property property = fetchPropertyById(propertyId);

        // Validate status
        if (property.getStatus() != PropertyStatus.PENDING) {
            throw new BusinessException("Property không ở trạng thái chờ quyết định");
        }

        Long hostId = property.getHost().getId();

        switch (req.getDecision()) {

            case APPROVED -> {
                property.setStatus(PropertyStatus.APPROVED);

                // apply image draft
                propertyImageService.applyDraft(propertyId);

                // === NEW: update contract status ===
                HostContract contract = property.getContract();
                if (contract != null && (contract.getStatus() == ContractStatus.PENDING)) {
                    contract.setStatus(ContractStatus.ACTIVE);
                }

                // notify host
                notificationService.notify(
                        hostId,
                        NotificationType.PROPERTY,
                        "Property #" + property.getId() + " đã được duyệt",
                        "Property \"" + property.getTitle()
                                + "\" của bạn đã được admin phê duyệt.",
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
                        false
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
//        if (property.getStatus() == PropertyStatus.DRAFT) {
//            property.setStatus(PropertyStatus.DELETED);
//            propertyRepository.delete(property);
//            return;
//        }

        // CASE 2: APPROVED -> send admin
        if (property.getStatus() == PropertyStatus.REJECTED
                || property.getStatus() == PropertyStatus.DRAFT) {
            property.setStatus(PropertyStatus.DELETE_PENDING);
            return;
        }

        throw new BusinessException("Không thể xóa property ở trạng thái hiện tại");
    }


    @Transactional
    public void adminApproveDelete(Long propertyId) {

        Property property = fetchPropertyById(propertyId);
        Long hostId = property.getHost().getId();

        if (property.getStatus() != PropertyStatus.DELETE_PENDING) {
            throw new BusinessException("Property không ở trạng thái chờ xóa");
        }

        notificationService.notify(
                hostId,
                NotificationType.PROPERTY,
                "Property đã được admin duyệt xóa",
                "Property " + property.getTitle() + "do bạn yêu cầu xóa đã được admin duyệt xóa!",
                false
        );

        // soft delete
        propertyRepository.delete(property);
    }


    public ResPropertyDetailDTO viewPropertyById(long propertyId) {
        Property property = fetchPropertyById(propertyId);
        return this.propertyMapper.convertToResPropertyDetailDTO(property);
    }

    public ResPropertyDTO viewHostPropertyById(long propertyId) {
        Property property = fetchPropertyById(propertyId);
        return this.propertyMapper.convertToResPropertyDTO(property);
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

    public List<ResPropertyDTO> handleListInactiveProperty() {

        Long hostId = SecurityUtil.getCurrentUserId();

        List<Property> properties =
                propertyRepository.findByHost_Id(hostId);

        return properties.stream()
                .filter(property ->
                        !contractPropertyRepository
                                .existsByPropertyIdAndContractStatus(
                                        property.getId(),
                                        ContractStatus.ACTIVE
                                )
                )
                .map(property -> {
                    ResPropertyDTO dto =
                            propertyMapper.convertToResPropertyDTO(property);

                    dto.setHasActiveContract(false);
                    return dto;
                })
                .toList();
    }


}
