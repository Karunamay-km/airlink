package com.karunamay.airlink.mapper.booking;

import com.karunamay.airlink.dto.booking.PassengerRequestDTO;
import com.karunamay.airlink.dto.booking.PassengerResponseDTO;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.booking.Passenger;
import com.karunamay.airlink.model.flight.Seat;
import com.karunamay.airlink.repository.booking.BookingRepository;
import com.karunamay.airlink.repository.flight.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassengerMapper {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    public PassengerResponseDTO toBasicResponseDTO(Passenger passenger) {
        if (passenger == null) return null;

        Long seatId = passenger.getSeat() != null ? passenger.getSeat().getId() : null;
        String seatNo = passenger.getSeat() != null ? passenger.getSeat().getSeatNo() : null;

        return PassengerResponseDTO.builder()
                .id(passenger.getId())
                .fullName(passenger.getFullName())
                .dob(passenger.getDob())
                .gender(passenger.getGender())
                .seatId(seatId)
                .seatNo(seatNo)
                .build();
    }

    public PassengerResponseDTO toResponseDTO(Passenger passenger) {
        if (passenger == null) {
            return null;
        }

        Long bookingId = passenger.getBooking() != null ? passenger.getBooking().getId() : null;
        Long seatId = passenger.getSeat() != null ? passenger.getSeat().getId() : null;
        String seatNo = passenger.getSeat() != null ? passenger.getSeat().getSeatNo() : null;

        return PassengerResponseDTO.builder()
                .id(passenger.getId())
                .bookingId(bookingId)
                .fullName(passenger.getFullName())
                .dob(passenger.getDob())
                .gender(passenger.getGender())
                .seatId(seatId)
                .seatNo(seatNo)
                .createdAt(passenger.getCreatedAt())
                .updatedAt(passenger.getUpdatedAt())
                .build();
    }


    public Passenger toEntity(PassengerRequestDTO request) {

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking with id " + request.getBookingId() + " not found")
                );

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat with id " + request.getSeatId() + " not found")
                );

        Passenger passenger = Passenger.builder()
                .fullName(request.getFullName())
                .dob(request.getDob())
                .booking(booking)
                .seat(seat)
                .gender(request.getGender())
                .build();

        seat.setPassenger(passenger);

        return passenger;
    }
}
