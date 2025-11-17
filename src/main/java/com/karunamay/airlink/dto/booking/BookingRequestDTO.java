package com.karunamay.airlink.dto.booking;

import com.karunamay.airlink.model.booking.BookingStatus;
import com.karunamay.airlink.validation.group.OnCreate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingRequestDTO {

    @NotNull(
        message = "User ID is required for booking",
        groups = OnCreate.class
    )
    private Long userId;

    @NotNull(
        message = "Flight ID is required for booking",
        groups = OnCreate.class
    )
    private Long flightId;

    @NotNull(message = "Total amount is required")
    @DecimalMin(
        value = "0.01",
        message = "Total amount must be greater than zero"
    )
    private BigDecimal totalAmount;

    @NotNull(message = "Passenger count is required")
    @Min(value = 1, message = "At least one passenger must be booked")
    private Integer passengerCount;

    @Valid
    @NotEmpty(
        message = "Booking must contain at least one passenger",
        groups = OnCreate.class
    )
    @Size(min = 1, message = "Passenger list cannot be empty")
    private List<@Valid PassengerRequestDTO> passengerList;

    private BookingStatus bookingStatus;
}
