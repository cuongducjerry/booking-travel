package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.travel.booking.dto.request.contract.ReqHostContractRequestDTO;
import vn.travel.booking.dto.request.contract.ReqRenewContractDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.contract.ResContractDTO;
import vn.travel.booking.entity.ContractProperty;
import vn.travel.booking.entity.HostContract;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.User;
import vn.travel.booking.mapper.ContractMapper;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.repository.ContractPropertyRepository;
import vn.travel.booking.repository.HostContractRepository;
import vn.travel.booking.repository.PropertyRepository;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.service.notification.NotificationService;
import vn.travel.booking.specification.HostContractSpecification;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.ContractStatus;
import vn.travel.booking.util.constant.NotificationType;
import vn.travel.booking.util.error.BusinessException;
import vn.travel.booking.util.error.IdInvalidException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HostContractService {

    private final HostContractRepository hostContractRepository;
    private final ContractMapper contractMapper;
    private final PaginationMapper paginationMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ContractPropertyRepository contractPropertyRepository;
    private final PropertyRepository propertyRepository;

    public HostContractService(
            HostContractRepository hostContractRepository,
            ContractMapper contractMapper,
            PaginationMapper paginationMapper,
            UserRepository userRepository,
            NotificationService notificationService,
            ContractPropertyRepository contractPropertyRepository,
            PropertyRepository propertyRepository) {
        this.hostContractRepository = hostContractRepository;
        this.contractMapper = contractMapper;
        this.paginationMapper = paginationMapper;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.contractPropertyRepository = contractPropertyRepository;
        this.propertyRepository = propertyRepository;
    }

    // host
    public ResultPaginationDTO getContracts(
            String contractCode,
            ContractStatus status,
            Pageable pageable
    ) {
        Long hostId = SecurityUtil.getCurrentUserId();

        Specification<HostContract> spec = Specification
                .where(HostContractSpecification.byHost(hostId))
                .and(HostContractSpecification.hasContractCode(contractCode))
                .and(HostContractSpecification.hasStatus(status));

        Page<HostContract> pageResult =
                hostContractRepository.findAll(spec, pageable);

        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();

        List<ResContractDTO> listContract = pageResult.getContent().stream()
                .map(contractMapper::convertToResContractDTO)
                .toList();

        return paginationMapper.convertToResultPaginationDTO(
                pageNumber,
                pageSize,
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                listContract
        );
    }

    // host
    public ResContractDTO getContractDetail(Long contractId) {

        Long hostId = SecurityUtil.getCurrentUserId();

        HostContract contract = hostContractRepository
                .findByIdAndHost_Id(contractId, hostId)
                .orElseThrow(() ->
                        new BusinessException("Contract không tồn tại hoặc không có quyền"));

        return this.contractMapper.convertToResContractDTO(contract);
    }

    // host
    @Transactional
    public ResContractDTO hostRequestContract(ReqHostContractRequestDTO req) {

        Long hostId = SecurityUtil.getCurrentUserId();

        User host = userRepository.findById(hostId)
                .orElseThrow(() ->
                        new IdInvalidException("Host không tồn tại")
                );

        // 1. Hosts are not allowed to have two contracts running simultaneously (PENDING / ACTIVE).
        boolean exists = hostContractRepository.existsByHost_IdAndStatusIn(
                hostId,
                List.of(ContractStatus.PENDING, ContractStatus.ACTIVE)
        );
        if (exists) {
            throw new BusinessException("Đã có hợp đồng đang xử lý");
        }

        // 2. Validate time
        if (!req.getStartDate().isBefore(req.getEndDate())) {
            throw new BusinessException("Thời gian hợp đồng không hợp lệ");
        }

        // ===============================
        // 3. Handle PROPERTY (OPTIONAL)
        // ===============================
        List<Property> properties = Collections.emptyList();

        if (!CollectionUtils.isEmpty(req.getPropertyIds())) {

            properties = propertyRepository.findAllById(req.getPropertyIds());

            if (properties.size() != req.getPropertyIds().size()) {
                throw new BusinessException("Có property không tồn tại");
            }

            // 4. Check ownership
            boolean notOwner = properties.stream()
                    .anyMatch(p -> !p.getHost().getId().equals(hostId));

            if (notOwner) {
                throw new BusinessException("Có property không thuộc host");
            }

            // 5. Check property đã thuộc hợp đồng khác chưa
            boolean anyActive = contractPropertyRepository
                    .existsActiveContractByPropertyIds(req.getPropertyIds());

            if (anyActive) {
                throw new BusinessException("Có property đã thuộc hợp đồng khác");
            }
        }

        // ===============================
        // 6. Create contract
        // ===============================
        ContractStatus status = CollectionUtils.isEmpty(req.getPropertyIds())
                ? ContractStatus.DRAFT
                : ContractStatus.PENDING;

        HostContract contract = HostContract.builder()
                .contractCode("HC-" + System.currentTimeMillis())
                .host(host)
                .commissionRate(req.getExpectedCommissionRate())
                .status(status)
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .signedAt(null)
                .build();

        hostContractRepository.save(contract);

        // ===============================
        // 7. Mapping contract-property (nếu có)
        // ===============================
        if (!CollectionUtils.isEmpty(properties)) {
            for (Property p : properties) {
                ContractProperty cp = ContractProperty.builder()
                        .contract(contract)
                        .property(p)
                        .build();
                contractPropertyRepository.save(cp);
            }
        }

        // ===============================
        // 8. Notify admin
        // ===============================
        notificationService.notifyAdmins(
                NotificationType.CONTRACT,
                "Yêu cầu hợp đồng mới " + contract.getContractCode(),
                "Host: " + host.getFullName()
                        + "\nHoa hồng đề xuất: " + (req.getExpectedCommissionRate() * 100) + "%"
                        + "\nThời gian: " + req.getStartDate() + " → " + req.getEndDate()
                        + "\nTrạng thái: " + status,
                true
        );

        return contractMapper.convertToResContractDTO(contract);
    }


    // admin
    public ResultPaginationDTO getAllContracts(
            String contractCode,
            ContractStatus status,
            Pageable pageable
    ) {

        Specification<HostContract> spec = Specification
                .where(HostContractSpecification.hasContractCode(contractCode))
                .and(HostContractSpecification.hasStatus(status));

        Page<HostContract> pageResult =
                hostContractRepository.findAll(spec, pageable);

        List<ResContractDTO> data = pageResult.getContent()
                .stream()
                .map(contractMapper::convertToResContractDTO)
                .toList();

        return paginationMapper.convertToResultPaginationDTO(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                data
        );
    }

    // admin
    public ResContractDTO getContractDetailForAdmin(Long id) {

        HostContract contract = hostContractRepository.findById(id)
                .orElseThrow(() ->
                        new IdInvalidException("Contract không tồn tại"));

        return contractMapper.convertToResContractDTO(contract);
    }

    // admin
    @Transactional
    public ResContractDTO approveContract(Long contractId) {

        HostContract contract = hostContractRepository.findById(contractId)
                .orElseThrow(() ->
                        new IdInvalidException("Contract " + contractId + " không tồn tại"));

        if (!contract.getStatus().equals(ContractStatus.PENDING)) {
            throw new BusinessException("Chỉ có thể duyệt contract PENDING");
        }

        Long hostId = contract.getHost().getId();

        boolean exists = hostContractRepository.existsByHost_IdAndStatus(
                hostId, ContractStatus.ACTIVE
        );
        if (exists) {
            throw new BusinessException("Host đã có contract ACTIVE");
        }

        if (!contract.getStartDate().isBefore(contract.getEndDate())) {
            throw new BusinessException("Thời gian hợp đồng không hợp lệ");
        }

        contract.setStatus(ContractStatus.ACTIVE);
        contract.setSignedAt(Instant.now());

        if (contract.getStartDate().isBefore(LocalDate.now())) {
            contract.setStartDate(LocalDate.now());
        }

        hostContractRepository.save(contract);

        // notify HOST
        notificationService.notify(
                hostId,
                NotificationType.CONTRACT,
                "Hợp đồng đã được duyệt",
                "Hợp đồng #" + contract.getContractCode()
                        + " đã được admin duyệt và có hiệu lực từ "
                        + contract.getStartDate(),
                true
        );

        return contractMapper.convertToResContractDTO(contract);
    }

    @Transactional
    public void rejectContract(Long contractId, String reason) {

        HostContract contract = hostContractRepository.findById(contractId)
                .orElseThrow(() ->
                        new IdInvalidException("Contract " + contractId + " không tồn tại"));

        if (!contract.getStatus().equals(ContractStatus.PENDING)) {
            throw new BusinessException("Chỉ reject contract PENDING");
        }

        contract.setStatus(ContractStatus.TERMINATED);
        contract.setTerminationReason(reason);
        contract.setTerminatedAt(Instant.now());

        hostContractRepository.save(contract);

        // notify HOST
        notificationService.notify(
                contract.getHost().getId(),
                NotificationType.CONTRACT,
                "Hợp đồng bị từ chối",
                "Hợp đồng #" + contract.getContractCode()
                        + " đã bị từ chối.\nLý do: " + reason,
                true
        );
    }


//    @Transactional
//    public ResContractDTO renewContract(Long oldContractId, ReqRenewContractDTO req) {
//
//        Long hostId = SecurityUtil.getCurrentUserId();
//
//        HostContract oldContract = hostContractRepository
//                .findByIdAndHost_Id(oldContractId, hostId)
//                .orElseThrow(() -> new BusinessException("Contract không tồn tại"));
//
//        if (oldContract.getStatus() != ContractStatus.EXPIRED) {
//            throw new BusinessException("Chỉ được gia hạn hợp đồng đã hết hạn");
//        }
//
//        boolean exists = hostContractRepository.existsByHost_IdAndStatusIn(
//                hostId,
//                List.of(ContractStatus.PENDING, ContractStatus.ACTIVE)
//        );
//        if (exists) {
//            throw new BusinessException("Đã có hợp đồng đang xử lý");
//        }
//
//        LocalDate today = LocalDate.now();
//
//        LocalDate startDate = today.isAfter(oldContract.getEndDate())
//                ? today
//                : oldContract.getEndDate();
//
//        if (!startDate.isBefore(req.getNewEndDate())) {
//            throw new BusinessException("Ngày kết thúc hợp đồng không hợp lệ");
//        }
//
//        HostContract newContract = HostContract.builder()
//                .contractCode("HC-" + System.currentTimeMillis())
//                .host(oldContract.getHost())
//                .commissionRate(req.getExpectedCommissionRate())
//                .status(ContractStatus.PENDING)
//                .startDate(startDate)
//                .endDate(req.getNewEndDate())
//                .build();
//
//        hostContractRepository.save(newContract);
//
//        // notify ADMIN
//        notificationService.notifyAdmins(
//                NotificationType.CONTRACT,
//                "Yêu cầu gia hạn hợp đồng",
//                "Host " + oldContract.getHost().getFullName()
//                        + " đã gửi yêu cầu gia hạn hợp đồng.\n"
//                        + "Contract cũ: " + oldContract.getContractCode()
//                        + "\nContract mới: " + newContract.getContractCode()
//                        + "\nThời gian: " + startDate + " → " + req.getNewEndDate(),
//                true
//        );
//
//        return contractMapper.convertToResContractDTO(newContract);
//    }

}
