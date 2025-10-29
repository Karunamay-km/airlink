package com.karunamay.airlink.repository.flight;

import com.karunamay.airlink.model.flight.Seat;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Hidden
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByFlight_FlightNo(String flightNo);

    Optional<Seat> findBySeatNo(String seatNo);

    List<Seat> findByAvailableTrue();

    Boolean existsByFlight_IdAndSeatNo(Long flightId, String seatNo);

    Boolean existsBySeatNo(String seatNo);

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.flight.id = :flightId AND s.available = true")
    Integer availableSeatCountByFlight(Long flightId);
}

