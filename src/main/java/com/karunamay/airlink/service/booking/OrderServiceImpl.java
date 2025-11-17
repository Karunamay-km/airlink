package com.karunamay.airlink.service.booking;

import com.karunamay.airlink.dto.booking.OrderRequestDTO;
import com.karunamay.airlink.dto.booking.OrderResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.booking.OrderMapper;
import com.karunamay.airlink.model.payment.Order;
import com.karunamay.airlink.model.payment.PaymentStatus;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.repository.order.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        log.info("Fetching order by id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order NOT FOUND for id={}", id);
                    return new ResourceNotFoundException("Order with id " + id + " not found");
                });

        OrderResponseDTO response = orderMapper.toBasicResponseDTO(order);

        log.info("Successfully fetched order id={}", id);
        return response;
    }

    @Override
    public PageResponseDTO<OrderResponseDTO> getOrdersByUserId(Long id, Pageable pageable) {
        log.info("Fetching orders for userId={}", id);

        Page<Order> page = orderRepository.findAllByUser_Id(id, pageable);
        PageResponseDTO<OrderResponseDTO> response = orderMapper.toPageResponseDTO(page);

        log.info("Found {} orders for userId={}", page.getNumberOfElements(), id);
        return response;
    }

    @Override
    public OrderResponseDTO getOrderByBookingId(Long id) {
        log.info("Fetching order by bookingId={}", id);

        Order order = orderRepository.findByBooking_Id(id)
                .orElseThrow(() -> {
                    log.error("Order NOT FOUND for bookingId={}", id);
                    return new ResourceNotFoundException("Order for booking id " + id + " not found");
                });

        OrderResponseDTO response = orderMapper.toBasicResponseDTO(order);

        log.info("Successfully fetched order for bookingId={}", id);
        return response;
    }

    @Override
    public PageResponseDTO<OrderResponseDTO> getOrdersByPaymentStatus(
            PaymentStatus paymentStatus, Pageable pageable
    ) {
        log.info("Fetching orders with paymentStatus={}", paymentStatus);

        Page<Order> page = orderRepository.findAllByPaymentStatus(paymentStatus, pageable);
        PageResponseDTO<OrderResponseDTO> response = orderMapper.toPageResponseDTO(page);

        log.info("Found {} orders with paymentStatus={}",
                page.getNumberOfElements(), paymentStatus);

        return response;
    }

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        log.info("Creating new order for userId={}, bookingId={}",
                requestDTO.getUserId(), requestDTO.getBookingId());

        Order order = orderMapper.toEntity(requestDTO);

        User user = order.getUser();
        user.addOrder(order);

        Order saved = orderRepository.save(order);

        log.info("Order created successfully id={} for userId={}",
                saved.getId(), requestDTO.getUserId());

        return orderMapper.toResponseDTO(saved);
    }

    @Override
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO requestDTO) {
        log.info("Updating order id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order NOT FOUND for id={}", id);
                    return new ResourceNotFoundException("Order with id " + id + " not found");
                });

        orderMapper.updateEntityFromRequest(order, requestDTO);
        orderRepository.save(order);

        log.info("Order updated successfully id={}", id);

        return orderMapper.toResponseDTO(order);
    }
}
