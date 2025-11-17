package com.karunamay.airlink.mapper.booking;

import com.karunamay.airlink.dto.booking.OrderRequestDTO;
import com.karunamay.airlink.dto.booking.OrderResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.PageMapper;
import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.payment.Order;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.repository.booking.BookingRepository;
import com.karunamay.airlink.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PageMapper pageMapper;

    public OrderResponseDTO toBasicResponseDTO(Order order) {
        if (order == null) return null;

        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .bookingId(order.getBooking().getId())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())

                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .addressLine1(order.getAddressLine1())
                .addressLine2(order.getAddressLine2())
                .city(order.getCity())
                .state(order.getState())
                .pinCode(order.getPinCode())
                .sessionId(order.getSessionId())

                .build();
    }

    public OrderResponseDTO toResponseDTO(Order order) {
        if (order == null) return null;

        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .bookingId(order.getBooking().getId())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())

                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .addressLine1(order.getAddressLine1())
                .addressLine2(order.getAddressLine2())
                .city(order.getCity())
                .state(order.getState())
                .pinCode(order.getPinCode())
                .sessionId(order.getSessionId())

                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public Order toEntity(OrderRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User with id " + request.getUserId() + " not found")
                );

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking with id " + request.getBookingId() + " not found")
                );

        return Order.builder()
                .user(user)
                .booking(booking)
                .totalAmount(request.getTotalAmount())
                .paymentStatus(request.getPaymentStatus())

                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pinCode(request.getPinCode())
                .sessionId(request.getSessionId())

                .build();
    }

    public void updateEntityFromRequest(Order order, OrderRequestDTO request) {
        if (order == null || request == null) return;

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User with id " + request.getUserId() + " not found")
                    );
            order.setUser(user);
        }

        if (request.getBookingId() != null) {
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Booking with id " + request.getBookingId() + " not found")
                    );
            order.setBooking(booking);
        }

        if (request.getTotalAmount() != null) {
            order.setTotalAmount(request.getTotalAmount());
        }

        if (request.getPaymentStatus() != null) {
            order.setPaymentStatus(request.getPaymentStatus());
        }

        if (request.getCustomerName() != null) {
            order.setCustomerName(request.getCustomerName());
        }

        if (request.getCustomerEmail() != null) {
            order.setCustomerEmail(request.getCustomerEmail());
        }

        if (request.getAddressLine1() != null) {
            order.setAddressLine1(request.getAddressLine1());
        }

        if (request.getAddressLine2() != null) {
            order.setAddressLine2(request.getAddressLine2());
        }

        if (request.getCity() != null) {
            order.setCity(request.getCity());
        }

        if (request.getState() != null) {
            order.setState(request.getState());
        }

        if (request.getPinCode() != null) {
            order.setPinCode(request.getPinCode());
        }

        if (request.getSessionId() != null) {
            order.setSessionId(request.getSessionId());
        }
    }

    public PageResponseDTO<OrderResponseDTO> toPageResponseDTO(Page<Order> orderPage) {
        return pageMapper.toPageResponse(orderPage, this::toBasicResponseDTO);
    }
}
