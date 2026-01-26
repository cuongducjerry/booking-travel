package vn.travel.booking.dto.response.contract;

import lombok.Data;
import vn.travel.booking.dto.response.property.ResPropertyDTO;
import vn.travel.booking.util.constant.ContractStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class ResContractDTO {
    private Long id;
    private String contractCode;

    private ContractStatus status;
    private double commissionRate;

    private LocalDate startDate;
    private LocalDate endDate;
    private Instant signedAt;
    private Instant terminatedAt;
    private String terminationReason;

    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;

    // host info
    private Long hostId;
    private String hostName;

    private List<ResPropDTO> properties;

    @Data
    public static class ResPropDTO {
        private long id;
        private String title;
        private String propertyTypeName;
        private String address;
    }

}
