package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.property.ResPropertyDTO;
import vn.travel.booking.dto.response.property.ResPropertyDetailDTO;
import vn.travel.booking.dto.response.property.ResPropertyWishlistDTO;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.PropertyImage;
import vn.travel.booking.entity.User;

import java.util.List;

@Component
public class PropertyMapper {

    public ResPropertyDTO convertToResPropertyDTO(Property property) {
        ResPropertyDTO dto = new ResPropertyDTO();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setAddress(property.getAddress());
        dto.setCity(property.getCity());
        dto.setPricePerNight(property.getPricePerNight());
        dto.setMaxGuests(property.getMaxGuests());
        dto.setPropertyTypeId(property.getPropertyType().getId());
        dto.setPropertyTypeName(property.getPropertyType().getName());
        dto.setHostId(property.getHost().getId());
        dto.setHostName(property.getHost().getFullName());
        dto.setStatus(property.getStatus().name());
        dto.setCurrency(property.getCurrency());
        dto.setCreatedAt(property.getCreatedAt());
        return dto;
    }

    public ResPropertyDetailDTO convertToResPropertyDetailDTO(Property property) {
        ResPropertyDetailDTO dto = new ResPropertyDetailDTO();

        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setAddress(property.getAddress());
        dto.setCity(property.getCity());
        dto.setPricePerNight(property.getPricePerNight());
        dto.setCurrency(property.getCurrency());
        dto.setMaxGuests(property.getMaxGuests());
        dto.setStatus(property.getStatus());
        dto.setCreatedAt(property.getCreatedAt());
        dto.setUpdatedAt(property.getUpdatedAt());
        dto.setPropertyType(property.getPropertyType().getName());
//        dto.setContractId(property.getContract().getId());

        // Images
        List<String> images = property.getImages()
                .stream()
                .map(PropertyImage::getImageUrl)
                .toList();
        dto.setImages(images);

        // Amenities
        List<ResPropertyDetailDTO.AmenityDTO> amenities = property.getAmenities()
                .stream()
                .map(a -> new ResPropertyDetailDTO.AmenityDTO(
                        a.getId(),
                        a.getName(),
                        a.getIcon()
                ))
                .toList();
        dto.setAmenities(amenities);

        // Reviews
//        List<ResPropertyDetailDTO.ReviewDTO> reviews = property.getReviews()
//                .stream()
//                .map(r -> new ResPropertyDetailDTO.ReviewDTO(
//                        r.getUser().getFullName(),
//                        r.getUser().getAvatarUrl(),
//                        r.getRating(),
//                        r.getComment(),
//                        r.getCreatedAt().toString()
//                ))
//                .toList();
//        dto.setReviews(reviews);

        // Host
        User host = property.getHost();
        ResPropertyDetailDTO.HostDTO hostDTO = new ResPropertyDetailDTO.HostDTO(
                host.getId(),
                host.getFullName(),
                host.getAvatarUrl(),
                host.getBio(),
                host.getAddress()
        );
        dto.setHost(hostDTO);

        return dto;
    }

    public ResPropertyWishlistDTO convertToResPropertyWishlistDTO(Property property) {
        ResPropertyWishlistDTO dto = new ResPropertyWishlistDTO();
        dto.setPropertyId(property.getId());
        dto.setPropertyName(property.getTitle());
        dto.setAddress(property.getAddress());

        String imageUrl = property.getImages()
                .stream()
                .findFirst()
                .map(PropertyImage::getImageUrl)
                .orElse(null);

        dto.setImageUrl(imageUrl);
        return dto;
    }

}
