package com.karunamay.airlink.dto.booking;

import com.karunamay.airlink.model.payment.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponseDTO {

    @NotNull
    private String message;

    @NotNull
    private Order order;

}
