package vn.travel.booking.dto.request.property;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ReqUpdatePropertyDTO {

    @Size(max = 255, message = "title tối đa 255 ký tự")
    private String title;

    @Size(max = 2000, message = "description tối đa 2000 ký tự")
    private String description;

    @Size(max = 255, message = "address tối đa 255 ký tự")
    private String address;

    @Size(max = 100, message = "city tối đa 100 ký tự")
    private String city;

    @Positive(message = "pricePerNight phải lớn hơn 0")
    private double pricePerNight;

    @Pattern(
            regexp = "VND|USD",
            message = "currency chỉ chấp nhận VND hoặc USD"
    )
    private String currency;

    @Min(value = 1, message = "maxGuests phải >= 1")
    private int maxGuests;

    private Long propertyTypeId;

    private List<@Positive(message = "amenityId phải > 0") Long> amenityIds;
}

