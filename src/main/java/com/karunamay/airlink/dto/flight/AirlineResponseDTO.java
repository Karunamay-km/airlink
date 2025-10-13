package com.karunamay.airlink.dto.flight;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.model.flight.Aircraft;
import com.karunamay.airlink.model.flight.Flight;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AirlineResponseDTO {

    private Long id;
    private String code;
    private String name;
    private String country;
    private String logoUrl;
    private Boolean active;
    private Set<AircraftResponseDTO> ownedAircrafts;
    private Set<FlightResponseDTO> operatedFlights;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
