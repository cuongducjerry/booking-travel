package vn.travel.booking.dto.response.property;

import lombok.*;
import vn.travel.booking.util.constant.PropertyStatus;

import java.time.Instant;
import java.util.List;

@Data
public class ResPropertyDetailDTO {
    private long id;
    private String title;
    private String description;
    private String address;
    private String city;
    private double pricePerNight;
    private String currency;
    private int maxGuests;
    private PropertyStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private List<String> images;
    private List<AmenityDTO> amenities;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AmenityDTO {
        private long amenityId;
        private String amenityName;
        private String amenityIcon;
    }

    private List<ReviewDTO> reviews;
    private String propertyType;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewDTO {
        private String user;
        private String avatarUrl;
        private int rating;
        private String comment;
        private String createdAt;
    }

    private HostDTO host;
    private long contractId;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HostDTO {
        private long hostId;
        private String hostName;
        private String avatarUrl;
        private String bio;
        private String address;
    }

}
