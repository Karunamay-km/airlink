package com.karunamay.airlink.mapper.booking;

import com.karunamay.airlink.dto.booking.BookingRequestDTO;
import com.karunamay.airlink.dto.booking.BookingResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.PageMapper;
import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.flight.Flight;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.repository.flight.FlightRepository;
import com.karunamay.airlink.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final PassengerMapper passengerMapper;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final PageMapper pageMapper;

    public BookingResponseDTO toBasicResponseDTO(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .pnrCode(booking.getPnrCode())
                .userId(booking.getUser().getId())
                .flightId(booking.getFlight().getId())
                .totalAmount(booking.getTotalAmount())
                .passengerCount(booking.getPassengerCount())
                .bookingStatus(booking.getBookingStatus())
                .paymentStatus(booking.getPaymentStatus())
                .bookedAt(booking.getCreatedAt())
                .passengers(booking
                        .getPassengers()
                        .stream()
                        .map(passengerMapper::toBasicResponseDTO)
                        .toList()
                )
                .build();
    }

    public BookingResponseDTO toResponseDTO(Booking booking) {
        if (booking == null) {
            return null;
        }
        Long flightId = booking.getFlight().getId();
        Flight flight = flightRepository
                .findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight with id " + flightId + " not found"));

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .pnrCode(booking.getPnrCode())
                .userId(booking.getUser().getId())
                .flightId(booking.getFlight().getId())
                .totalAmount(booking.getTotalAmount())
                .passengerCount(booking.getPassengerCount())
                .bookingStatus(booking.getBookingStatus())
                .paymentStatus(booking.getPaymentStatus())
                .bookedAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .passengers(booking
                        .getPassengers()
                        .stream()
                        .map(passengerMapper::toBasicResponseDTO)
                        .toList()
                )
                .build();
    }

    public Booking toEntity(BookingRequestDTO request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + request.getUserId() + " not found"));

        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight with id " + request.getFlightId() + " not found"));

        return Booking.builder()
                .totalAmount(request.getTotalAmount())
                .passengerCount(request.getPassengerCount())
                .user(user)
                .flight(flight)
                .passengers(request.getPassengerList()
                        .stream()
                        .map(passengerMapper::toEntity)
                        .collect(Collectors.toSet())
                )
                .build();
    }

    public PageResponseDTO<BookingResponseDTO> toPageResponseDTO(Page<Booking> bookingPage) {
        return pageMapper.toPageResponse(bookingPage, this::toBasicResponseDTO);
    }

}
