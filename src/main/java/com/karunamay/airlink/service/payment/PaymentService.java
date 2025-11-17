package com.karunamay.airlink.service.payment;

import com.karunamay.airlink.dto.booking.PaymentResponseDTO;
import com.karunamay.airlink.model.payment.Order;

public interface PaymentService {

    PaymentResponseDTO processPayment(String sessionId);

    PaymentResponseDTO checkPaymentStatus(Long orderId);
}
