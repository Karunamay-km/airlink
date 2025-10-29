package com.karunamay.airlink.repository.flight;

import com.karunamay.airlink.model.flight.Flight;
import io.swagger.v3.oas.annotations.Hidden;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Hidden
public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findByFlightNo(String flightNo);

    @Query("SELECT f FROM Flight f")
    Page<Flight> findAllFlights(Pageable pageable);

    Page<Flight> findAllBySrcAirport_NameIgnoreCase(
        String name,
        Pageable pageable
    );

    Page<Flight> findAllByDestAirport_NameIgnoreCase(
        String name,
        Pageable pageable
    );

    Boolean existsByFlightNo(String flightNo);

    @Query(
        """
        SELECT f
        FROM Flight f
        WHERE f.srcAirport.Id = :srcAirportId
        AND f.destAirport.Id = :destAirportId
        AND f.departureTime >= :departureTime
        AND (SELECT COUNT(s) FROM Seat s WHERE s.flight = f AND s.available = true) >= :seatCount
        """
    )
    Page<Flight> findAllFlightBySearchParameters(
        @Param("srcAirportId") Long srcAirportId,
        @Param("destAirportId") Long destAirportId,
        @Param("departureTime") LocalDateTime departureTime,
        @Param("seatCount") Integer seats,
        Pageable pageable
    );
}
