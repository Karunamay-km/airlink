package com.karunamay.airlink.dto.flight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.karunamay.airlink.model.flight.Flight;
import com.karunamay.airlink.model.flight.SeatClass;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import jakarta.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatResponseDTO {

@NotNull
private Long id;

@NotNull
private Long flightId;

@NotNull
private String seatNo;

@NotNull
private SeatClass seatClass;

@NotNull
private Boolean available = true;

@NotNull
private BigDecimal priceModifier;

@NotNull
@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
private LocalDateTime createdAt;

@NotNull
@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
private LocalDateTime updatedAt;
}
