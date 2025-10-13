package com.karunamay.airlink.repository.flight;

import com.karunamay.airlink.model.flight.Airport;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Hidden
public interface AirportRepository extends JpaRepository<Airport, Long> {

    Optional<Airport> findByNameIgnoreCase(String name);

    Optional<Airport> findByCodeIgnoreCase(String code);

    List<Airport> findByCityIgnoreCase(String city);

    List<Airport> findByActiveTrue();

    Boolean existsByCode(String code);

    Boolean existsByName(String name);

}
