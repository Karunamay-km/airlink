package com.karunamay.airlink.repository.flight;

import com.karunamay.airlink.model.flight.Aircraft;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Hidden
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    Optional<Aircraft> findByModelIgnoreCase(String model);

    Optional<Aircraft> findByRegistrationNumberIgnoreCase(String registrationNumber);

    List<Aircraft> findAllByAirline_NameIgnoreCase(String name);

    List<Aircraft> findByActiveTrue();

    Boolean existsByModel(String model);

    Boolean existsByRegistrationNumber(String registrationNumber);
}
