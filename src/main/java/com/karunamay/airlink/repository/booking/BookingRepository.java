package com.karunamay.airlink.repository.booking;

import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.booking.BookingStatus;
import com.karunamay.airlink.model.user.User;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Hidden
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByUser(User user, Pageable pageable);

    Page<Booking> findAllByBookingStatus(BookingStatus status, Pageable pageable);

    Optional<Booking> findByPnrCode(String pnrCode);

    Optional<Booking> findByUserUsername(String username);

}
