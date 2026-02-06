package vn.travel.booking.dto.response.propertyimage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ResPropertyImage {
    private Long id;
    private String imageUrl;
}
