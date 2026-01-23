package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.booking.ResBookingDTO;
import vn.travel.booking.entity.Booking;

@Component
public class BookingMapper {

    public ResBookingDTO convertToResBookingDTO(Booking booking) {

        ResBookingDTO dto = new ResBookingDTO();

        // -------- booking info --------
        dto.setId(booking.getId());
        dto.setCheckIn(booking.getCheckIn());
        dto.setCheckOut(booking.getCheckOut());
        dto.setNights(booking.getNights());

        dto.setPricePerNightSnapshot(booking.getPricePerNightSnapshot());
        dto.setCurrency(booking.getCurrency());

        dto.setGrossAmount(booking.getGrossAmount());
        dto.setCommissionRate(booking.getCommissionRate());
        dto.setCommissionFee(booking.getCommissionFee());
        dto.setHostEarning(booking.getHostEarning());

        dto.setStatus(booking.getStatus());

        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());

        // -------- user info --------
        if (booking.getUser() != null) {
            dto.setUserId(booking.getUser().getId());
            dto.setUserName(booking.getUser().getFullName());
            dto.setUserEmail(booking.getUser().getEmail());
        }

        // -------- property info --------
        if (booking.getProperty() != null) {
            dto.setPropertyId(booking.getProperty().getId());
            dto.setPropertyName(booking.getProperty().getTitle());
        }

        return dto;
    }


}
