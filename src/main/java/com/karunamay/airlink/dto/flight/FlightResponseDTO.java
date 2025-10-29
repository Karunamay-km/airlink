package com.karunamay.airlink.dto.flight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.dto.booking.BookingResponseDTO;
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
    private AirportResponseDTO srcAirport;
    private AirportResponseDTO destAirport;
    private String flightNo;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal basePrice;
    private FlightStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
