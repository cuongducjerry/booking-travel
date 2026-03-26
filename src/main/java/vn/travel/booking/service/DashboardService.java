package vn.travel.booking.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.travel.booking.dto.response.ResDashboardDTO;
import vn.travel.booking.dto.response.ResHostDashboardDTO;
import vn.travel.booking.dto.response.ResRevenueByMonthDTO;
import vn.travel.booking.repository.*;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.PayoutStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final HostPayoutRepository payoutRepository;
    private final HostContractRepository contractRepository;

    public ResDashboardDTO getAdminDashboard() {

        List<Object[]> result = payoutRepository.getRevenueSummary(PayoutStatus.PAID);

        double totalGross = 0;
        double totalCommission = 0;
        double totalNet = 0;

        if (result != null && !result.isEmpty()) {
            Object[] revenue = result.get(0);

            totalGross = revenue[0] != null ? ((Number) revenue[0]).doubleValue() : 0;
            totalCommission = revenue[1] != null ? ((Number) revenue[1]).doubleValue() : 0;
            totalNet = revenue[2] != null ? ((Number) revenue[2]).doubleValue() : 0;
        }

        return ResDashboardDTO.builder()
                .countUser(userRepository.count())
                .countProperty(propertyRepository.count())
                .countBooking(bookingRepository.count())
                .countContract(contractRepository.count())
                .countPayout(payoutRepository.count())

                .totalGross(totalGross)
                .totalCommission(totalCommission)
                .totalNet(totalNet)
                .build();
    }

    public List<ResRevenueByMonthDTO> getAdminRevenueLast12Months() {

        Instant fromDate = Instant.now().minus(365, ChronoUnit.DAYS);
        List<Object[]> rows = payoutRepository.getRevenueLast12Months(fromDate);

        Map<String, ResRevenueByMonthDTO> map = new HashMap<>();

        for (Object[] r : rows) {
            int year = ((Number) r[0]).intValue();
            int month = ((Number) r[1]).intValue();

            String key = year + "-" + month;

            map.put(key, ResRevenueByMonthDTO.builder()
                    .year(year)
                    .month(month)
                    .totalGross(r[2] != null ? ((Number) r[2]).doubleValue() : 0)
                    .totalCommission(r[3] != null ? ((Number) r[3]).doubleValue() : 0)
                    .totalNet(r[4] != null ? ((Number) r[4]).doubleValue() : 0)
                    .build());
        }

        List<ResRevenueByMonthDTO> result = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate d = now.minusMonths(i);
            String key = d.getYear() + "-" + d.getMonthValue();

            result.add(map.getOrDefault(key,
                    ResRevenueByMonthDTO.builder()
                            .year(d.getYear())
                            .month(d.getMonthValue())
                            .totalGross(0)
                            .totalCommission(0)
                            .totalNet(0)
                            .build()
            ));
        }

        return result;
    }

    public ResHostDashboardDTO getHostDashboard() {

        Long hostId = SecurityUtil.getCurrentUserId();

        long countProperty = propertyRepository.count(
                (root, query, cb) -> cb.equal(root.get("host").get("id"), hostId)
        );

        long countBooking = bookingRepository.count(
                (root, query, cb) -> cb.equal(root.get("property").get("host").get("id"), hostId)
        );

        long countPayout = payoutRepository.count(
                (root, query, cb) -> cb.equal(root.get("host").get("id"), hostId)
        );

        List<Object[]> result = payoutRepository.sumRevenueByHost(hostId);

        double totalGross = 0;
        double totalCommission = 0;
        double totalNet = 0;

        if (result != null && !result.isEmpty()) {
            Object[] revenue = result.get(0);

            totalGross = revenue[0] != null ? ((Number) revenue[0]).doubleValue() : 0;
            totalCommission = revenue[1] != null ? ((Number) revenue[1]).doubleValue() : 0;
            totalNet = revenue[2] != null ? ((Number) revenue[2]).doubleValue() : 0;
        }

        return ResHostDashboardDTO.builder()
                .countProperty(countProperty)
                .countBooking(countBooking)
                .countPayout(countPayout)
                .totalGross(totalGross)
                .totalCommission(totalCommission)
                .totalNet(totalNet)
                .build();
    }

    public List<ResRevenueByMonthDTO> getHostRevenueLast12Months() {

        Long hostId = SecurityUtil.getCurrentUserId();

        Instant fromDate = Instant.now().minus(365, ChronoUnit.DAYS);
        List<Object[]> rows = payoutRepository.getRevenueLast12MonthsByHost(hostId, fromDate, PayoutStatus.PAID);

        Map<String, ResRevenueByMonthDTO> map = new HashMap<>();


        for (Object[] r : rows) {
            int year = ((Number) r[0]).intValue();
            int month = ((Number) r[1]).intValue();

            String key = year + "-" + month;

            map.put(key, ResRevenueByMonthDTO.builder()
                    .year(year)
                    .month(month)
                    .totalGross(r[2] != null ? ((Number) r[2]).doubleValue() : 0)
                    .totalCommission(r[3] != null ? ((Number) r[3]).doubleValue() : 0)
                    .totalNet(r[4] != null ? ((Number) r[4]).doubleValue() : 0)
                    .build());
        }


        List<ResRevenueByMonthDTO> result = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate d = now.minusMonths(i);
            String key = d.getYear() + "-" + d.getMonthValue();

            result.add(map.getOrDefault(key,
                    ResRevenueByMonthDTO.builder()
                            .year(d.getYear())
                            .month(d.getMonthValue())
                            .totalGross(0)
                            .totalCommission(0)
                            .totalNet(0)
                            .build()
            ));
        }

        return result;
    }

}
