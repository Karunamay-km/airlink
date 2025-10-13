package com.karunamay.airlink.dto.flight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.model.flight.Airline;
import com.karunamay.airlink.model.flight.Flight;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AircraftResponseDTO {

    private Long id;
    private String model;
    private String registrationNumber;
    private Integer capacity;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
