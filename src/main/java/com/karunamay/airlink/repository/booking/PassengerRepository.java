package com.karunamay.airlink.repository.booking;

import com.karunamay.airlink.model.booking.Passenger;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Hidden
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

}
