package com.karunamay.airlink.repository.order;

import com.karunamay.airlink.model.payment.Order;
import com.karunamay.airlink.model.payment.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByUser_Id(Long userId, Pageable pageable);

    Optional<Order> findByBooking_Id(Long bookingId);

    Page<Order> findAllByPaymentStatus(PaymentStatus status, Pageable pageable);

}
