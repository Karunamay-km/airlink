package com.karunamay.airlink.service;

import com.karunamay.airlink.dto.booking.BookingResponseDTO;
import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.repository.booking.BookingRepository;
import com.karunamay.airlink.service.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final BookingRepository bookingRepository;
    private final BaseService baseService;

    public boolean isOwnerOfTheBooking(Long bookingId, Authentication authentication) {

        Booking booking = baseService.findByIdOrThrow(bookingId, bookingRepository);

        Long bookingUserId = booking.getUser().getId();
        User userDetails = (User) authentication.getPrincipal();
        Long userId = userDetails.getId();

        return bookingUserId.equals(userId);

    }

}
