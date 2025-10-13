package com.karunamay.airlink.repository.flight;

import com.karunamay.airlink.model.flight.Airline;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@Hidden
public interface AirlineRepository extends JpaRepository<Airline, Long> {

    Optional<Airline> findByCodeIgnoreCase(String code);

    Optional<Airline> findByNameIgnoreCase(String name);

    List<Airline> findByActiveTrue();

    Boolean existsByCode(String code);

    Boolean existsByName(String name);

}
