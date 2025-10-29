package com.karunamay.airlink.repository.flight;

import com.karunamay.airlink.model.flight.Airport;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Hidden
public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport> findByNameIgnoreCase(String name);

    Optional<Airport> findByCodeIgnoreCase(String code);

    Page<Airport> findByCityIgnoreCase(String city, Pageable pageable);

    Page<Airport> findByActiveTrue(Pageable pageable);

    Page<Airport> findAll(Pageable pageable);

    Boolean existsByCode(String code);

    Boolean existsByName(String name);
}
