package com.karunamay.airlink.repository.booking;

import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.booking.BookingStatus;
import com.karunamay.airlink.model.flight.Seat;
import com.karunamay.airlink.model.user.User;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Hidden
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByUser(User user);

    List<Booking> findAllByBookingStatus(BookingStatus status);

    Optional<Booking> findByPnrCode(String pnrCode);

    Optional<Booking> findByUserUsername(String username);

}
