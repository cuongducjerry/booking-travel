package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.request.payout.ReqCreateHostPayoutDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.payout.ResHostPayoutDTO;
import vn.travel.booking.entity.*;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.mapper.PayoutMapper;
import vn.travel.booking.repository.BookingRepository;
import vn.travel.booking.repository.HostContractRepository;
import vn.travel.booking.repository.HostPayoutRepository;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.service.notification.NotificationService;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.ContractStatus;
import vn.travel.booking.util.constant.NotificationType;
import vn.travel.booking.util.constant.PayoutStatus;
import vn.travel.booking.util.error.BusinessException;
import vn.travel.booking.util.error.ForbiddenException;
import vn.travel.booking.util.error.IdInvalidException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HostPayoutService {

    private final HostPayoutRepository payoutRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HostContractRepository contractRepository;
    private final PayoutMapper payoutMapper;
    private final PaginationMapper paginationMapper;
    private final NotificationService notificationService;

    public HostPayoutService(
            HostPayoutRepository payoutRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository,
            HostContractRepository contractRepository,
            PayoutMapper payoutMapper,
            PaginationMapper paginationMapper,
            NotificationService notificationService
    ) {
        this.payoutRepository = payoutRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.payoutMapper = payoutMapper;
        this.paginationMapper = paginationMapper;
        this.notificationService = notificationService;
    }

    @Transactional
    public ResHostPayoutDTO createPayout(ReqCreateHostPayoutDTO req) {

        HostContract contract = contractRepository.findById(req.getContractId())
                .orElseThrow(() -> new IdInvalidException("Contract not found"));

        User host = userRepository.findById(contract.getHost().getId())
                .orElseThrow(() -> new IdInvalidException("Host not found"));

        // The contract must be ACTIVE.
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new BusinessException("Contract không ở trạng thái ACTIVE");
        }

        // The contract must remain valid until the END of the payout period.
        if (contract.getEndDate().isBefore(req.getPeriodTo())) {
            throw new BusinessException(
                    "Contract đã hết hạn trước khi kết thúc kỳ payout"
            );
        }

        // Block duplicate payouts
        if (payoutRepository.existsByHost_IdAndContract_IdAndPeriodFromAndPeriodTo(
                host.getId(),
                contract.getId(),
                req.getPeriodFrom(),
                req.getPeriodTo()
        )) {
            throw new BusinessException("Payout cho kỳ này đã tồn tại");
        }

        // Booking completed, correct host, correct contract, but payout not yet received.
        List<Booking> bookings =
                bookingRepository.findDoneBookingsNotPayoutYet(
                        host.getId(),
                        contract.getId(),
                        req.getPeriodFrom(),
                        req.getPeriodTo()
                );

        if (bookings.isEmpty()) {
            throw new BusinessException("Không có booking hợp lệ để payout");
        }

        HostPayout payout = HostPayout.builder()
                .host(host)
                .contract(contract)
                .periodFrom(req.getPeriodFrom())
                .periodTo(req.getPeriodTo())
                .currency("VND")
                .status(PayoutStatus.PENDING)
                .build();

        double gross = 0;
        double fee = 0;
        double net = 0;

        for (Booking b : bookings) {

            HostPayoutItem item = HostPayoutItem.builder()
                    .booking(b)
                    .payout(payout)
                    .bookingAmount(b.getGrossAmount())
                    .commissionFee(b.getCommissionFee())
                    .netAmount(b.getHostEarning())
                    .build();

            payout.getItems().add(item);

            gross += b.getGrossAmount();
            fee += b.getCommissionFee();
            net += b.getHostEarning();
        }

        payout.setGrossAmount(gross);
        payout.setCommissionFee(fee);
        payout.setNetAmount(net);

        payoutRepository.save(payout);

        return payoutMapper.convertToResHostPayoutDTO(payout);
    }

    @Transactional
    public ResHostPayoutDTO markPaid(Long payoutId, String transactionRef) {

        HostPayout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new IdInvalidException("Payout not found"));

        // Only allow the transition from PENDING to PAID.
        if (payout.getStatus() != PayoutStatus.PENDING) {
            throw new BusinessException(
                    "Chỉ được mark PAID khi payout ở trạng thái PENDING"
            );
        }

        payout.setStatus(PayoutStatus.PAID);
        payout.setPaidAt(Instant.now());
        payout.setTransactionRef(transactionRef);

        // ================================
        // NOTIFY HOST – PAYOUT SUCCESS
        // ================================
        try {
            notificationService.notify(
                    payout.getHost().getId(),
                    NotificationType.PAYOUT,
                    "Payout #" + payout.getId() + " đã được thanh toán",
                    "Admin đã chuyển tiền payout cho kỳ "
                            + payout.getPeriodFrom() + " → " + payout.getPeriodTo()
                            + ".\n"
                            + "Số tiền nhận: " + payout.getNetAmount() + " " + payout.getCurrency()
                            + ".\n"
                            + "Mã giao dịch: " + transactionRef,
                    true
            );
        } catch (Exception e) {
            System.err.println("Send mail failed: " + e.getMessage());
        }

        return payoutMapper.convertToResHostPayoutDTO(payout);
    }

    @Transactional
    public ResHostPayoutDTO markRejected(Long payoutId, String reason) {

        HostPayout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new IdInvalidException("Payout not found"));

        // Only allow reject when PENDING
        if (payout.getStatus() != PayoutStatus.PENDING) {
            throw new BusinessException(
                    "Chỉ được reject payout khi trạng thái là PENDING"
            );
        }

        payout.setStatus(PayoutStatus.REJECTED);
        payout.setRejectedAt(Instant.now());
        payout.setRejectReason(reason);

        // ================================
        // NOTIFY HOST – PAYOUT REJECTED
        // ================================
        try {
            notificationService.notify(
                    payout.getHost().getId(),
                    NotificationType.PAYOUT,
                    "Payout #" + payout.getId() + " bị từ chối",
                    "Payout cho kỳ "
                            + payout.getPeriodFrom() + " → " + payout.getPeriodTo()
                            + " đã bị từ chối.\n"
                            + "Lý do: " + reason,
                    true
            );
        } catch (Exception e) {
            System.err.println("Send mail failed: " + e.getMessage());
        }

        return payoutMapper.convertToResHostPayoutDTO(payout);
    }

    public ResHostPayoutDTO getDetail(Long payoutId) {

        HostPayout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() ->
                        new IdInvalidException("Payout not found"));

        Long currentUserId = SecurityUtil.getCurrentUserId();

        // If you are a HOST -> you can only view your own payout.
        if (SecurityUtil.isHost()) {
            if (!payout.getHost().getId().equals(currentUserId)) {
                throw new ForbiddenException("You are not allowed to view this payout");
            }
        }

        // Admin / Super Admin will automatically pass.
        return payoutMapper.convertToResHostPayoutDTO(payout);
    }

    public ResultPaginationDTO getList(Specification spec, Pageable pageable) {
        Page<HostPayout> pagePayout = this.payoutRepository.findAll(spec, pageable);
        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        int totalPages = pagePayout.getTotalPages();
        long totalElements = pagePayout.getTotalElements();

        List<ResHostPayoutDTO> listPayout = pagePayout.getContent().stream()
                .map(item -> this.payoutMapper.convertToResHostPayoutDTO(item))
                .collect(Collectors.toList());

        return this.paginationMapper.convertToResultPaginationDTO(pageNumber, pageSize, totalPages, totalElements, listPayout);

    }


}