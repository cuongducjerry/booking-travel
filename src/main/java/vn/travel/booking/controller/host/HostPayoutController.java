package vn.travel.booking.controller.host;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.entity.HostPayout;
import vn.travel.booking.service.HostPayoutService;
import vn.travel.booking.specification.PayoutSpecification;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.constant.PayoutStatus;

@RestController
@RequestMapping("/api/v1/host/host-payouts")
public class HostPayoutController {

    private final HostPayoutService hostPayoutService;

    public HostPayoutController(HostPayoutService hostPayoutService) {
        this.hostPayoutService = hostPayoutService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PAYOUT_LIST_OWN')")
    @ApiMessage("Get list payout of host")
    public ResponseEntity<ResultPaginationDTO> listMyPayouts(
            @RequestParam(required = false) PayoutStatus status,
            Pageable pageable
    ) {
        Long hostId = SecurityUtil.getCurrentUserId();

        Specification<HostPayout> spec = Specification
                .where(PayoutSpecification.byHost(hostId))
                .and(PayoutSpecification.status(status));

        return ResponseEntity.status(HttpStatus.OK).body(hostPayoutService.getList(spec, pageable));
    }
}
