package com.karunamay.airlink.dto.flight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.model.flight.SeatClass;
import com.karunamay.airlink.validation.group.OnCreate;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatRequestDTO {

    @NotBlank(message = "Flight is required", groups = OnCreate.class)
    private Long flightId;

    @NotBlank(message = "Seat No is required", groups = OnCreate.class)
    private String seatNo;

    @NotBlank(message = "Seat class is required", groups = OnCreate.class)
    private SeatClass seatClass;

    private Boolean available;

    @NotBlank(message = "Price modifier is required", groups = OnCreate.class)
    private BigDecimal priceModifier;
}
