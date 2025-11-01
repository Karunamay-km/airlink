package com.karunamay.airlink.service.booking;

import com.karunamay.airlink.dto.booking.BookingRequestDTO;
import com.karunamay.airlink.dto.booking.BookingResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.model.booking.BookingStatus;
import com.karunamay.airlink.model.user.User;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    BookingResponseDTO getBookingById(Long id);

    BookingResponseDTO getBookingByUser(User user);

    BookingResponseDTO getBookingByPnrCode(String pnrCode);

    PageResponseDTO<BookingResponseDTO> getBookingsByStatus(BookingStatus status, Pageable pageable);

    PageResponseDTO<BookingResponseDTO> getAllBookings(Pageable pageable);

    BookingResponseDTO createBooking(BookingRequestDTO requestDTO);
    
}
