package com.karunamay.airlink.repository.flight;

import com.karunamay.airlink.model.flight.Flight;
import com.karunamay.airlink.model.flight.Seat;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Hidden
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByFlight(Flight flight);

    Optional<Seat> findBySeatNo(String seatNo);

    List<Seat> findByAvailableTrue();
}

