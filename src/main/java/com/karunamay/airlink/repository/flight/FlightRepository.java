package com.karunamay.airlink.repository.flight;

import com.karunamay.airlink.model.flight.Flight;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Hidden
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByFlightNo(String flightNo);

    List<Flight> findAllBySrcAirport_Name(String name);

    List<Flight> findAllByDestAirport_Name(String name);

    Boolean existsByFlightNo(String flightNo);

}
