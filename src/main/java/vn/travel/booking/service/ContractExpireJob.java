package vn.travel.booking.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.entity.HostContract;
import vn.travel.booking.repository.HostContractRepository;
import vn.travel.booking.util.constant.ContractStatus;

import java.time.Instant;
import java.util.List;

@Service
public class ContractExpireJob {

    private final HostContractRepository repo;

    public ContractExpireJob(HostContractRepository repo) {
        this.repo = repo;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")  // every day 0h
    @Transactional
    public void expireContracts() {
        Instant now = Instant.now();
        List<HostContract> contracts = repo.findExpiredActiveContracts(now);

        for (HostContract c : contracts) {
            c.setStatus(ContractStatus.EXPIRED);
        }
    }
}

