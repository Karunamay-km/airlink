package com.karunamay.airlink.service.booking;

import com.karunamay.airlink.dto.booking.BookingRequestDTO;
import com.karunamay.airlink.dto.booking.BookingResponseDTO;
import com.karunamay.airlink.model.booking.BookingStatus;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.repository.booking.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BookingService {

    BookingResponseDTO getBookingById(Long id);

    BookingResponseDTO getBookingByUser(User user);

    BookingResponseDTO getBookingByPnrCode(String pnrCode);

    List<BookingResponseDTO> getBookingsByStatus(BookingStatus status);

    BookingResponseDTO createBooking(BookingRequestDTO requestDTO);
}
