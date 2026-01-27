package vn.travel.booking.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.request.fee.ReqUpdateFeeStatusDTO;
import vn.travel.booking.dto.response.fee.ResHostFeeDTO;
import vn.travel.booking.service.HostFeeService;
import vn.travel.booking.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/admin/fees")
@RequiredArgsConstructor
public class AdminFeeController {

    private final HostFeeService hostFeeService;

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('FEE_UPDATE')")
    @ApiMessage("Update fee status by admin")
    public ResponseEntity<ResHostFeeDTO> updateFeeStatus(
            @PathVariable Long id,
            @RequestBody ReqUpdateFeeStatusDTO req
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(hostFeeService.updateFeeStatus(id, req.getStatus()));
    }
}

