package vn.travel.booking.controller.fee;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.fee.ResHostFeeDTO;
import vn.travel.booking.service.HostFeeService;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.constant.FeeStatus;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/v1/fees")
@RequiredArgsConstructor
public class FeeController {

    private final HostFeeService hostFeeService;

    // LIST (HOST sees their own view | ADMIN sees the full view)
    @GetMapping
    @PreAuthorize("hasAuthority('FEE_LIST')")
    @ApiMessage("Get list fees")
    public ResponseEntity<ResultPaginationDTO> getFees(
            @RequestParam(required = false) FeeStatus status,
            Pageable pageable
    ) {
        ResultPaginationDTO res = hostFeeService.getFees(status, pageable);
        return ResponseEntity.ok(res);
    }

    // DETAIL (HOST only views their own content | ADMIN views everything)
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FEE_DETAIL')")
    @ApiMessage("Get fee detail by id")
    public ResponseEntity<ResHostFeeDTO> getFeeDetail(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(hostFeeService.getFeeDetail(id));
    }
}