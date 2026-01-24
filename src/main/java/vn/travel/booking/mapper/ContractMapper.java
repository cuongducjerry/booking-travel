package vn.travel.booking.mapper;

import org.apache.http.annotation.Contract;
import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.contract.ResContractDTO;
import vn.travel.booking.entity.HostContract;

import java.util.List;

@Component
public class ContractMapper {

    public ResContractDTO convertToResContractDTO(HostContract contract) {

        ResContractDTO dto = new ResContractDTO();

        // basic info
        dto.setId(contract.getId());
        dto.setContractCode(contract.getContractCode());
        dto.setStatus(contract.getStatus());
        dto.setCommissionRate(contract.getCommissionRate());

        // dates
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setSignedAt(contract.getSignedAt());
        dto.setTerminatedAt(contract.getTerminatedAt());
        dto.setTerminationReason(contract.getTerminationReason());

        dto.setActive(contract.isActive());
        dto.setCreatedAt(contract.getCreatedAt());
        dto.setUpdatedAt(contract.getUpdatedAt());

        // host info
        if (contract.getHost() != null) {
            dto.setHostId(contract.getHost().getId());
            dto.setHostName(contract.getHost().getFullName());
        }

        // properties
        List<ResContractDTO.ResPropDTO> propDTOs =
                contract.getProperties() == null
                        ? List.of()
                        : contract.getProperties().stream()
                        .map(p -> {
                            ResContractDTO.ResPropDTO rp =
                                    new ResContractDTO.ResPropDTO();
                            rp.setId(p.getId());
                            rp.setTitle(p.getTitle());
                            rp.setAddress(p.getAddress());

                            if (p.getPropertyType() != null) {
                                rp.setPropertyTypeName(
                                        p.getPropertyType().getName()
                                );
                            }
                            return rp;
                        })
                        .toList();

        dto.setProperties(propDTOs);

        return dto;
    }


}
