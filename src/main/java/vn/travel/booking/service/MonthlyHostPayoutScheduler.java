package vn.travel.booking.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.entity.Booking;
import vn.travel.booking.entity.HostContract;
import vn.travel.booking.entity.HostPayout;
import vn.travel.booking.entity.HostPayoutItem;
import vn.travel.booking.repository.BookingRepository;
import vn.travel.booking.repository.HostContractRepository;
import vn.travel.booking.repository.HostPayoutRepository;
import vn.travel.booking.util.constant.ContractStatus;
import vn.travel.booking.util.constant.PayoutStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
public class MonthlyHostPayoutScheduler {

    private final HostContractRepository contractRepository;
    private final BookingRepository bookingRepository;
    private final HostPayoutRepository payoutRepository;

    public MonthlyHostPayoutScheduler(
            HostContractRepository contractRepository,
            BookingRepository bookingRepository,
            HostPayoutRepository payoutRepository
    ) {
        this.contractRepository = contractRepository;
        this.bookingRepository = bookingRepository;
        this.payoutRepository = payoutRepository;
    }

    /**
     * Runs at 00:05 on the 1st of every month.
     */
    @Scheduled(cron = "0 5 0 1 * ?")
    @Transactional
    public void generateMonthlyPayouts() {

        YearMonth lastMonth = YearMonth.now().minusMonths(1);

        LocalDate from = lastMonth.atDay(1);          // 01/MM/YYYY
        LocalDate to = lastMonth.atEndOfMonth();      // end of month

        // Only contracts that are still valid during the payout period will be considered.
        List<HostContract> contracts =
                contractRepository.findActiveContractsValidForPayout(to);

        for (HostContract contract : contracts) {

            // Block duplicate entries
            boolean payoutExists =
                    payoutRepository.existsByContractAndPeriodFromAndPeriodTo(
                            contract, from, to
                    );

            if (payoutExists) {
                continue;
            }

            Long hostId = contract.getHost().getId();

            List<Booking> bookings =
                    bookingRepository.findDoneBookingsForMonthlyPayout(
                            hostId, from, to
                    );

            if (bookings.isEmpty()) {
                continue;
            }

            createPayout(contract, bookings, from, to);
        }
    }


    private void createPayout(
            HostContract contract,
            List<Booking> bookings,
            LocalDate from,
            LocalDate to
    ) {

        // ADD THIS CHECK
        if (contract.getEndDate().isBefore(to)) {
            return;
        }

        HostPayout payout = HostPayout.builder()
                .host(contract.getHost())
                .contract(contract)
                .periodFrom(from)
                .periodTo(to)
                .status(PayoutStatus.PENDING)
                .currency("VND")
                .build();

        double gross = 0, fee = 0, net = 0;

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

        // If bookings are duplicated → UNIQUE constraint triggers an error.
        payoutRepository.save(payout);
    }
}

