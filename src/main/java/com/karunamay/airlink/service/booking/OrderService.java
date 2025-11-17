package com.karunamay.airlink.service.booking;

import com.karunamay.airlink.dto.booking.OrderRequestDTO;
import com.karunamay.airlink.dto.booking.OrderResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.model.payment.PaymentStatus;
import org.springframework.data.domain.Pageable;


public interface OrderService {

    OrderResponseDTO getOrderById(Long id);

    PageResponseDTO<OrderResponseDTO> getOrdersByUserId(Long id, Pageable pageable);

    OrderResponseDTO getOrderByBookingId(Long id);

    PageResponseDTO<OrderResponseDTO> getOrdersByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

    OrderResponseDTO createOrder(OrderRequestDTO requestDTO);

    OrderResponseDTO updateOrder(Long id, OrderRequestDTO requestDTO);
}
