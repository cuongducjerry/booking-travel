package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.request.amenity.ReqCreateAmenityDTO;
import vn.travel.booking.dto.response.amenity.ResAmenityDTO;
import vn.travel.booking.entity.Amenity;

@Component
public class AmenityMapper {

    public ResAmenityDTO convertToResAmenityDTO(Amenity amenity) {
        ResAmenityDTO amenityDTO = new ResAmenityDTO();
        amenityDTO.setId(amenity.getId());
        amenityDTO.setName(amenity.getName());
        amenityDTO.setIcon(amenity.getIcon());
        amenityDTO.setCreatedAt(amenity.getCreatedAt());
        amenityDTO.setUpdatedAt(amenity.getUpdatedAt());
        amenityDTO.setCreatedBy(amenity.getCreatedBy());
        amenityDTO.setUpdatedBy(amenity.getUpdatedBy());
        return amenityDTO;
    }

}
