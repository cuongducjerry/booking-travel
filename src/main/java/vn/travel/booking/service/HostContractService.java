package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.request.contract.ReqHostContractRequestDTO;
import vn.travel.booking.dto.request.contract.ReqRenewContractDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.contract.ResContractDTO;
import vn.travel.booking.entity.HostContract;
import vn.travel.booking.entity.User;
import vn.travel.booking.mapper.ContractMapper;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.repository.HostContractRepository;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.ContractStatus;
import vn.travel.booking.util.error.BusinessException;
import vn.travel.booking.util.error.IdInvalidException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HostContractService {

    private final HostContractRepository hostContractRepository;
    private final ContractMapper contractMapper;
    private final PaginationMapper paginationMapper;
    private final UserRepository userRepository;

    public HostContractService(
            HostContractRepository hostContractRepository,
            ContractMapper contractMapper,
            PaginationMapper paginationMapper,
            UserRepository userRepository) {
        this.hostContractRepository = hostContractRepository;
        this.contractMapper = contractMapper;
        this.paginationMapper = paginationMapper;
        this.userRepository = userRepository;
    }

    // host
    public ResultPaginationDTO getContracts(Pageable pageable) {
        Long hostId = SecurityUtil.getCurrentUserId();

        Page<HostContract> pageResult = hostContractRepository.findByHost_Id(hostId, pageable);

        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        int totalPages = pageResult.getTotalPages();
        long totalElements = pageResult.getTotalElements();

        List<ResContractDTO> listContract = pageResult.getContent().stream()
                .map(item -> this.contractMapper.convertToResContractDTO(item))
                .collect(Collectors.toList());

        return this.paginationMapper.convertToResultPaginationDTO(pageNumber, pageSize, totalPages, totalElements, listContract);
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
                .orElseThrow(() -> new IdInvalidException(
                        "Host với id = " + hostId + " không tồn tại!"
                ));

        // Not allowed to exist pending / active
        boolean exists = hostContractRepository.existsByHost_IdAndStatusIn(
                hostId,
                List.of(ContractStatus.PENDING, ContractStatus.ACTIVE)
        );
        if (exists) {
            throw new BusinessException("Đã có hợp đồng đang xử lý");
        }

        // Validate time
        if (req.getStartDate() == null || req.getEndDate() == null) {
            throw new BusinessException("Ngày bắt đầu và kết thúc là bắt buộc");
        }

        if (!req.getStartDate().isBefore(req.getEndDate())) {
            throw new BusinessException("Thời gian hợp đồng không hợp lệ");
        }

        // Create PENDING contract (host request)
        HostContract contract = HostContract.builder()
                .contractCode("HC-" + System.currentTimeMillis())
                .host(host)
                .commissionRate(req.getExpectedCommissionRate())
                .status(ContractStatus.PENDING)
                .startDate(req.getStartDate()) // suggested host
                .endDate(req.getEndDate())
                .signedAt(null)
                .build();

        hostContractRepository.save(contract);

        return contractMapper.convertToResContractDTO(contract);
    }


    // admin
    public ResultPaginationDTO getAllContracts(Pageable pageable) {

        Page<HostContract> pageResult = hostContractRepository.findAll(pageable);

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

        // Ensure only 1 ACTIVE
        boolean exists = hostContractRepository.existsByHost_IdAndStatus(
                hostId, ContractStatus.ACTIVE
        );
        if (exists) {
            throw new BusinessException("Host đã có contract ACTIVE");
        }

        // Re-validate time
        if (!contract.getStartDate().isBefore(contract.getEndDate())) {
            throw new BusinessException("Thời gian hợp đồng không hợp lệ");
        }

        contract.setStatus(ContractStatus.ACTIVE);
        contract.setSignedAt(Instant.now());

        // Optional: if startDate < now then set = now
        if (contract.getStartDate().isBefore(Instant.now())) {
            contract.setStartDate(Instant.now());
        }

        hostContractRepository.save(contract);
        return contractMapper.convertToResContractDTO(contract);
    }


    @Transactional
    public void rejectContract(Long contractId, String reason) {

        HostContract contract = hostContractRepository.findById(contractId)
                .orElseThrow(() ->
                        new IdInvalidException("Contract" + contractId + " không tồn tại"));

        if (!contract.getStatus().equals(ContractStatus.PENDING)) {
            throw new BusinessException("Chỉ reject contract PENDING");
        }

        contract.setStatus(ContractStatus.TERMINATED);
        contract.setTerminationReason(reason);
        contract.setTerminatedAt(Instant.now());

        hostContractRepository.save(contract);
    }

    @Transactional
    public ResContractDTO renewContract(Long oldContractId, ReqRenewContractDTO req) {

        Long hostId = SecurityUtil.getCurrentUserId();
        Instant now = Instant.now();

        HostContract oldContract = hostContractRepository
                .findByIdAndHost_Id(oldContractId, hostId)
                .orElseThrow(() -> new BusinessException("Contract không tồn tại"));

        // Only expired contracts
        if (oldContract.getStatus() != ContractStatus.EXPIRED) {
            throw new BusinessException("Chỉ được gia hạn hợp đồng đã hết hạn");
        }

        // No active or pending contracts allowed
        boolean exists = hostContractRepository.existsByHost_IdAndStatusIn(
                hostId,
                List.of(ContractStatus.PENDING, ContractStatus.ACTIVE)
        );
        if (exists) {
            throw new BusinessException("Đã có hợp đồng đang xử lý");
        }

        // START DATE: hệ thống tự tính
        Instant startDate = now.isAfter(oldContract.getEndDate())
                ? now
                : oldContract.getEndDate();

        // END DATE validation
        if (!startDate.isBefore(req.getNewEndDate())) {
            throw new BusinessException("Ngày kết thúc hợp đồng không hợp lệ");
        }

        HostContract newContract = HostContract.builder()
                .contractCode("HC-" + System.currentTimeMillis())
                .host(oldContract.getHost())
                .commissionRate(req.getExpectedCommissionRate())
                .status(ContractStatus.PENDING)
                .startDate(startDate)
                .endDate(req.getNewEndDate())
                .build();

        hostContractRepository.save(newContract);

        return contractMapper.convertToResContractDTO(newContract);
    }

}
