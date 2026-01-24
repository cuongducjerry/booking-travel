package vn.travel.booking.dto.response.contract;

import lombok.Data;
import vn.travel.booking.dto.response.property.ResPropertyDTO;
import vn.travel.booking.util.constant.ContractStatus;

import java.time.Instant;
import java.util.List;

@Data
public class ResContractDTO {
    private long id;
    private String contractCode;

    private ContractStatus status;
    private double commissionRate;

    private Instant startDate;
    private Instant endDate;
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
