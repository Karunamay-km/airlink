package com.karunamay.airlink.dto.flight;

import com.karunamay.airlink.model.flight.*;
import com.karunamay.airlink.validation.group.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Airline id is required", groups = OnCreate.class)
    private Long airlineId;

    @NotNull(message = "Aircraft id is required", groups = OnCreate.class)
    private Long aircraftId;

    @NotNull(message = "Source airport id is required", groups = OnCreate.class)
    private Long srcAirportId;

    @NotNull(message = "Destination airport id is required", groups = OnCreate.class)
    private Long destAirportId;

    @NotBlank(message = "Flight no is required", groups = OnCreate.class)
    private String flightNo;

    @NotNull(message = "Departure time is required", groups = OnCreate.class)
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required", groups = OnCreate.class)
    private LocalDateTime arrivalTime;

    @NotNull(message = "Base price is required", groups = OnCreate.class)
    private BigDecimal basePrice;

}
