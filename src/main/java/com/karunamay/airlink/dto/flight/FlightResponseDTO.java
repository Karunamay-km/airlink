package com.karunamay.airlink.dto.flight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.dto.booking.BookingResponseDTO;
import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.flight.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightResponseDTO {

    private Long id;
    private AirlineResponseDTO airline;
    private AircraftResponseDTO aircraft;
    private Airport srcAirport;
    private Airport destAirport;
    private String flightNo;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal basePrice;
    private FlightStatus status;
    private Set<BookingResponseDTO> bookings;
    private Set<Seat> seats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
