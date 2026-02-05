package vn.travel.booking.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.contract.ResContractDTO;
import vn.travel.booking.service.HostContractService;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.constant.ContractStatus;

@RestController
@RequestMapping("/api/v1/admin/contracts")
public class AdminContractController {

    private final HostContractService hostContractService;

    public AdminContractController(HostContractService hostContractService) {
        this.hostContractService = hostContractService;
    }

    /* ================= LIST ALL CONTRACTS ================= */
    @GetMapping
    @PreAuthorize("hasAuthority('CONTRACT_LIST_ALL')")
    @ApiMessage("Admin get all contracts")
    public ResponseEntity<ResultPaginationDTO> getAll(
            @RequestParam(required = false) String contractCode,
            @RequestParam(required = false) ContractStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                hostContractService.getAllContracts(contractCode, status, pageable)
        );
    }

    /* ================= VIEW DETAIL ================= */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTRACT_DETAIL_ALL')")
    @ApiMessage("Admin view contract detail")
    public ResponseEntity<ResContractDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(hostContractService.getContractDetailForAdmin(id));
    }

    /* ================= APPROVE CONTRACT ================= */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('CONTRACT_APPROVE')")
    @ApiMessage("Admin approve contract")
    public ResponseEntity<ResContractDTO> approve(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(hostContractService.approveContract(id));
    }

    /* ================= REJECT CONTRACT ================= */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('CONTRACT_REJECT')")
    @ApiMessage("Admin reject contract")
    public ResponseEntity<Void> reject(
            @PathVariable Long id,
            @RequestParam String reason
    ) {
        hostContractService.rejectContract(id, reason);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
