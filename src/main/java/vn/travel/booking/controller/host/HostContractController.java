package vn.travel.booking.controller.host;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.request.contract.ReqHostContractRequestDTO;
import vn.travel.booking.dto.request.contract.ReqRenewContractDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.contract.ResContractDTO;
import vn.travel.booking.service.HostContractService;
import vn.travel.booking.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/host/contracts")
public class HostContractController {

    private final HostContractService hostContractService;

    public HostContractController(HostContractService hostContractService) {
        this.hostContractService = hostContractService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('CONTRACT_LIST_PERSONAL')")
    @ApiMessage("Get list contract of personal")
    public ResponseEntity<ResultPaginationDTO> myContracts(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.hostContractService.getContracts(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTRACT_VIEW_PERSONAL')")
    @ApiMessage("Get contract detail")
    public ResponseEntity<ResContractDTO> contractDetail(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(hostContractService.getContractDetail(id));
    }

    @PostMapping("/request")
    @PreAuthorize("hasAuthority('CONTRACT_REQUEST')")
    @ApiMessage("Send request create a contract")
    public ResponseEntity<ResContractDTO> requestContract(@RequestBody ReqHostContractRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hostContractService.hostRequestContract(req));
    }

    @PostMapping("/{id}/renew")
    @PreAuthorize("hasAuthority('CONTRACT_RENEW')")
    @ApiMessage("Send request renew a contract")
    public ResponseEntity<ResContractDTO> renew(
            @PathVariable Long id,
            @RequestBody ReqRenewContractDTO req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hostContractService.renewContract(id, req));
    }

}
