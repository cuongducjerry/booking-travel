package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.propertytype.ResPropertyTypeDTO;
import vn.travel.booking.entity.PropertyType;

@Component
public class PropertyTypeMapper {

    public ResPropertyTypeDTO convertToResPropertyTypeDTO(PropertyType propertyType) {
        ResPropertyTypeDTO resPropertyTypeDTO = new ResPropertyTypeDTO();
        resPropertyTypeDTO.setId(propertyType.getId());
        resPropertyTypeDTO.setName(propertyType.getName());
        resPropertyTypeDTO.setCreatedAt(propertyType.getCreatedAt());
        resPropertyTypeDTO.setUpdatedAt(propertyType.getUpdatedAt());
        resPropertyTypeDTO.setCreatedBy(propertyType.getCreatedBy());
        resPropertyTypeDTO.setUpdatedBy(propertyType.getUpdatedBy());
        return resPropertyTypeDTO;
    }

}
