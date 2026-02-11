package vn.travel.booking.dto.request.property;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReqCreatePropertyDTO {

    @NotBlank(message = "title không được để trống")
    @Size(max = 255, message = "title tối đa 255 ký tự")
    private String title;

    @NotBlank(message = "description không được để trống")
    @Size(max = 2000, message = "description tối đa 2000 ký tự")
    private String description;

    @NotBlank(message = "address không được để trống")
    @Size(max = 255, message = "address tối đa 255 ký tự")
    private String address;

    @NotBlank(message = "city không được để trống")
    @Size(max = 100, message = "city tối đa 100 ký tự")
    private String city;

    @Positive(message = "pricePerNight phải lớn hơn 0")
    private double pricePerNight;

    @NotBlank(message = "currency không được để trống")
    @Pattern(
            regexp = "VND|USD",
            message = "currency chỉ chấp nhận VND hoặc USD"
    )
    private String currency;

    @Min(value = 1, message = "maxGuests phải >= 1")
    private int maxGuests;

    @NotNull(message = "propertyTypeId không được để trống")
    private Long propertyTypeId;

    @NotNull(message = "contractId không được để trống")
    private Long contractId;

    @NotNull(message = "latitude không được để trống")
    private Double latitude;

    @NotNull(message = "longitude không được để trống")
    private Double longitude;

}


