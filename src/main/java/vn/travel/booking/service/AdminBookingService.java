package vn.travel.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.booking.ResBookingDTO;
import vn.travel.booking.entity.Booking;
import vn.travel.booking.mapper.BookingMapper;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.repository.BookingRepository;
import vn.travel.booking.specification.BookingSpecification;
import vn.travel.booking.util.constant.BookingStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBookingService {

    private final BookingRepository bookingRepository;
    private final PaginationMapper paginationMapper;
    private final BookingMapper bookingMapper;

    public ResultPaginationDTO getBookings(
            BookingStatus status,
            Pageable pageable
    ) {

        Specification<Booking> spec = Specification
                .where(BookingSpecification.hasStatus(status));

        Page<Booking> page = bookingRepository.findAll(spec, pageable);

        List<ResBookingDTO> list = page.getContent()
                .stream()
                .map(item -> bookingMapper.convertToResBookingDTO(item))
                .toList();

        return paginationMapper.convertToResultPaginationDTO(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                list
        );
    }
}
