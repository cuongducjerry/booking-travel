package vn.travel.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.fee.ResHostFeeDTO;
import vn.travel.booking.entity.HostFee;
import vn.travel.booking.mapper.FeeMapper;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.repository.HostFeeRepository;
import vn.travel.booking.specification.HostFeeSpecification;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.FeeStatus;
import vn.travel.booking.util.error.BusinessException;
import vn.travel.booking.util.error.IdInvalidException;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HostFeeService {

    private final HostFeeRepository hostFeeRepository;
    private final FeeMapper feeMapper;
    private final PaginationMapper paginationMapper;


    public ResultPaginationDTO getFees(FeeStatus status, Pageable pageable) {

        boolean isAdmin = SecurityUtil.isAdmin() || SecurityUtil.isSuperAdmin();

        Specification<HostFee> spec = Specification
                .where(
                        isAdmin
                                ? null // ADMIN / SUPER_ADMIN see all
                                : HostFeeSpecification.visibleByCurrentHost()
                )
                .and(HostFeeSpecification.hasStatus(status));

        Page<HostFee> pageFee = hostFeeRepository.findAll(spec, pageable);

        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();

        List<ResHostFeeDTO> list = pageFee.getContent()
                .stream()
                .map(feeMapper::convertResHostFeeDTO)
                .toList();

        return paginationMapper.convertToResultPaginationDTO(
                pageNumber,
                pageSize,
                pageFee.getTotalPages(),
                pageFee.getTotalElements(),
                list
        );
    }

    public ResHostFeeDTO getFeeDetail(Long feeId) throws AccessDeniedException {

        HostFee fee = hostFeeRepository.findById(feeId)
                .orElseThrow(() -> new IdInvalidException("Fee không tồn tại"));

        boolean isAdmin = SecurityUtil.isAdmin() || SecurityUtil.isSuperAdmin();

        if (!isAdmin &&
                !fee.getHostId().equals(SecurityUtil.getCurrentUserId())) {
            throw new AccessDeniedException("Không có quyền xem fee này");
        }

        return feeMapper.convertResHostFeeDTO(fee);
    }

    @Transactional
    public ResHostFeeDTO updateFeeStatus(Long feeId, FeeStatus newStatus) {

        if (newStatus == null) {
            throw new BusinessException("Fee status không hợp lệ");
        }

        HostFee fee = hostFeeRepository.findById(feeId)
                .orElseThrow(() -> new IdInvalidException("Fee không tồn tại"));

        FeeStatus current = fee.getStatus();

        // =============================
        // STATE TRANSITION VALIDATION
        // =============================

        if (current == FeeStatus.PAID) {
            throw new BusinessException("Fee đã PAID, không thể thay đổi");
        }

        if (current == FeeStatus.OVERDUE && newStatus == FeeStatus.PENDING) {
            throw new BusinessException("Không thể revert OVERDUE → PENDING");
        }

        if (current == newStatus) {
            return feeMapper.convertResHostFeeDTO(fee);
        }

        // =============================
        // APPLY STATUS
        // =============================

        fee.setStatus(newStatus);

        if (newStatus == FeeStatus.PAID) {
            fee.setPaidAt(Instant.now());
        }

        hostFeeRepository.save(fee);

        return feeMapper.convertResHostFeeDTO(fee);
    }

}

