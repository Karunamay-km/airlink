package com.karunamay.airlink.dto.flight;

import com.karunamay.airlink.model.flight.*;
import com.karunamay.airlink.validation.group.OnCreate;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FlightRequestDTO {
    @NotBlank(message = "Airline id is required", groups = OnCreate.class)
    private Long airlineId;

    @NotBlank(message = "Aircraft id is required", groups = OnCreate.class)
    private Long aircraftId;

    @NotBlank(message = "Source airport id is required", groups = OnCreate.class)
    private Long srcAirportId;

    @NotBlank(message = "Destination airport id is required", groups = OnCreate.class)
    private Long destAirportId;

    @NotBlank(message = "Flight no is required", groups = OnCreate.class)
    private String flightNo;

    @NotBlank(message = "Departure time is required", groups = OnCreate.class)
    private LocalDateTime departureTime;

    @NotBlank(message = "Arrival time is required", groups = OnCreate.class)
    private LocalDateTime arrivalTime;

    @NotBlank(message = "Base price is required", groups = OnCreate.class)
    private BigDecimal basePrice;

}
