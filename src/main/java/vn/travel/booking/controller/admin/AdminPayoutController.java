package vn.travel.booking.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.request.payout.ReqCreateHostPayoutDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.payout.ResHostPayoutDTO;
import vn.travel.booking.entity.HostPayout;
import vn.travel.booking.entity.User;
import vn.travel.booking.service.HostPayoutService;
import vn.travel.booking.specification.PayoutSpecification;
import vn.travel.booking.specification.UserSpecification;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.constant.PayoutStatus;

@RestController
@RequestMapping("/api/v1/admin/host-payouts")
public class AdminPayoutController {

    private final HostPayoutService payoutService;

    public AdminPayoutController(HostPayoutService payoutService) {
        this.payoutService = payoutService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PAYOUT_CREATE')")
    @ApiMessage("Create host payout")
    public ResponseEntity<ResHostPayoutDTO> create(@RequestBody ReqCreateHostPayoutDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payoutService.createPayout(req));
    }

    @PutMapping("/{id}/mark-paid")
    @PreAuthorize("hasAuthority('PAYOUT_MARK_PAID')")
    @ApiMessage("Mark payout as paid")
    public ResponseEntity<ResHostPayoutDTO> markPaid(
            @PathVariable Long id,
            @RequestParam String transactionRef
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payoutService.markPaid(id, transactionRef));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYOUT_VIEW_ALL')")
    @ApiMessage("Get detail payout by id")
    public ResponseEntity<ResHostPayoutDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(payoutService.getDetail(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PAYOUT_LIST_ALL')")
    @ApiMessage("Get list all payout")
    public ResponseEntity<ResultPaginationDTO> list(
            @RequestParam(required = false) PayoutStatus status,
            Pageable pageable
    ) {
        Specification<HostPayout> spec = Specification
                .where(PayoutSpecification.status(status));

        return ResponseEntity.status(HttpStatus.OK).body(payoutService.getList(spec, pageable));
    }

}
