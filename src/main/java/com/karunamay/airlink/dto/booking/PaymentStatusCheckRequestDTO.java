package com.karunamay.airlink.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentStatusCheckRequestDTO {

    @NotNull(message = "Order id is required")
    private Long orderId;

}
