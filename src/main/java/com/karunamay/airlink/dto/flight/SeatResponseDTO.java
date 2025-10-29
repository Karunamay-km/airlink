package com.karunamay.airlink.dto.flight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.model.flight.Flight;
import com.karunamay.airlink.model.flight.SeatClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatResponseDTO {

    private Long id;
    private FlightResponseDTO flight;
    private String seatNo;
    private SeatClass seatClass;
    private Boolean available = true;
    private BigDecimal priceModifier;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
