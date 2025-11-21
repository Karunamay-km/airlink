package com.karunamay.airlink.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentInitRequestDTO {

    @NotNull(message = "Payment amount is required")
    public BigDecimal amount;

    @NotNull(message = "User id is required")
    public Long userId;

    @NotNull(message = "Booking id is required")
    public Long bookingId;
}
