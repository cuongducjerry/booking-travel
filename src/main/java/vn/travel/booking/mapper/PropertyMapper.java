package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.property.ResPropertyDTO;
import vn.travel.booking.dto.response.property.ResPropertyDetailDTO;
import vn.travel.booking.dto.response.property.ResPropertyWishlistDTO;
import vn.travel.booking.dto.response.propertyimage.ResPropertyImage;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.PropertyImage;
import vn.travel.booking.entity.PropertyImageDraft;
import vn.travel.booking.entity.User;
import vn.travel.booking.util.constant.BookingStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        dto.setContractId(property.getContract().getId());

        if(property.getLatitude() != null) {
            dto.setLatitude(property.getLatitude());
        }

        if(property.getLongitude() != null) {
            dto.setLongitude(property.getLongitude());
        }

        // ================== IMAGE ========================
        dto.setImages(
                Optional.ofNullable(property.getImages())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(img -> new ResPropertyImage(
                                img.getId(),
                                img.getImageUrl()
                        ))
                        .toList()
        );

        // ================== IMAGE DRAFTS ==================
        dto.setImageDrafts(
                Optional.ofNullable(property.getImageDrafts())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(d -> new ResPropertyImage(
                                d.getId(),
                                d.getImageUrl()
                        ))
                        .toList()
        );

        // ================== BOOKINGS ==================
        List<ResPropertyDTO.ResPropertyBookingDTO> bookingDTOs =
                Optional.ofNullable(property.getBookings())
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                        .map(b -> {
                            ResPropertyDTO.ResPropertyBookingDTO bd =
                                    new ResPropertyDTO.ResPropertyBookingDTO();
                            bd.setCheckIn(b.getCheckIn());
                            bd.setCheckOut(b.getCheckOut());
                            return bd;
                        })
                        .toList();

        dto.setBookings(bookingDTOs);

        List<ResPropertyDetailDTO.AmenityDTO> amenities =
                Optional.ofNullable(property.getAmenities())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(a -> new ResPropertyDetailDTO.AmenityDTO(
                                a.getId(),
                                a.getName(),
                                a.getIcon()
                        ))
                        .collect(Collectors.toList());

        dto.setAmenities(amenities);

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
        dto.setContractId(property.getContract().getId());

        if(property.getLatitude() != null) {
            dto.setLatitude(property.getLatitude());
        }
        if(property.getLongitude() != null) {
            dto.setLongitude(property.getLongitude());
        }

        // Images
        List<String> images = new ArrayList<>();

        List<PropertyImage> listImages = property.getImages();
        if (listImages != null) {
            for (PropertyImage image : listImages) {
                images.add(image.getImageUrl());
            }
        }

        dto.setImages(images);

        // Amenities
        List<ResPropertyDetailDTO.AmenityDTO> amenities =
                Optional.ofNullable(property.getAmenities())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(a -> new ResPropertyDetailDTO.AmenityDTO(
                                a.getId(),
                                a.getName(),
                                a.getIcon()
                        ))
                        .collect(Collectors.toList());

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

        List<ResPropertyDetailDTO.ResPropertyBookingDTO> bookingDTOs =
                Optional.ofNullable(property.getBookings())
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                        .map(b -> {
                            ResPropertyDetailDTO.ResPropertyBookingDTO bd = new ResPropertyDetailDTO.ResPropertyBookingDTO();
                            bd.setCheckIn(b.getCheckIn());
                            bd.setCheckOut(b.getCheckOut());
                            return bd;
                        })
                        .toList();

        dto.setBookings(bookingDTOs);

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
