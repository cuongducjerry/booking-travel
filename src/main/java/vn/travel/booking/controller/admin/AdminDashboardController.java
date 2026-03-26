package vn.travel.booking.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.travel.booking.dto.response.ResDashboardDTO;
import vn.travel.booking.dto.response.ResRevenueByMonthDTO;
import vn.travel.booking.service.DashboardService;
import vn.travel.booking.util.annotation.ApiMessage;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasAnyAuthority('USER_LIST_ALL')")
    @ApiMessage("Admin dashboard")
    public ResponseEntity<ResDashboardDTO> getDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/admin/revenue/monthly")
    @PreAuthorize("hasAuthority('USER_LIST_ALL')")
    public ResponseEntity<List<ResRevenueByMonthDTO>> getRevenueMonthly() {
        return ResponseEntity.ok(dashboardService.getAdminRevenueLast12Months());
    }

}
