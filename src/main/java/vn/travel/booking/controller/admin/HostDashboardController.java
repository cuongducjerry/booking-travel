package vn.travel.booking.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.travel.booking.dto.response.ResDashboardDTO;
import vn.travel.booking.dto.response.ResHostDashboardDTO;
import vn.travel.booking.dto.response.ResRevenueByMonthDTO;
import vn.travel.booking.service.DashboardService;
import vn.travel.booking.util.annotation.ApiMessage;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class HostDashboardController {

    private final DashboardService dashboardService;

    public HostDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/host/dashboard")
    @PreAuthorize("hasAuthority('BOOKING_LIST_OWN')")
    @ApiMessage("Host dashboard")
    public ResponseEntity<ResHostDashboardDTO> getDashboard() {
        return ResponseEntity.ok(dashboardService.getHostDashboard());
    }

    @GetMapping("/host/revenue/monthly")
    @PreAuthorize("hasAuthority('BOOKING_LIST_OWN')")
    @ApiMessage("Host revenue last 12 months")
    public ResponseEntity<List<ResRevenueByMonthDTO>> getHostRevenue() {
        return ResponseEntity.ok(dashboardService.getHostRevenueLast12Months());
    }

}
