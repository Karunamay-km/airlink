package com.karunamay.airlink.mapper.booking;

import com.karunamay.airlink.dto.booking.PassengerRequestDTO;
import com.karunamay.airlink.dto.booking.PassengerResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.PageMapper;
import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.booking.Passenger;
import com.karunamay.airlink.model.flight.Seat;
import com.karunamay.airlink.repository.booking.BookingRepository;
import com.karunamay.airlink.repository.flight.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassengerMapper {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final PageMapper pageMapper;

    public PassengerResponseDTO toBasicResponseDTO(Passenger passenger) {
        if (passenger == null) return null;

        Long seatId = passenger.getSeat() != null ? passenger.getSeat().getId() : null;
        String seatNo = passenger.getSeat() != null ? passenger.getSeat().getSeatNo() : null;

        return PassengerResponseDTO.builder()
                .id(passenger.getId())
                .firstName(passenger.getFirstName())
                .middleName(passenger.getMiddleName())
                .lastName(passenger.getLastName())
                .suffix(passenger.getSuffix())
                .dob(passenger.getDob())
                .gender(passenger.getGender())
                .email(passenger.getEmail())
                .phone(passenger.getPhone())
                .checkedBagCount(passenger.getCheckedBagCount())
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
                .firstName(passenger.getFirstName())
                .middleName(passenger.getMiddleName())
                .lastName(passenger.getLastName())
                .suffix(passenger.getSuffix())
                .dob(passenger.getDob())
                .gender(passenger.getGender())
                .email(passenger.getEmail())
                .phone(passenger.getPhone())
                .checkedBagCount(passenger.getCheckedBagCount())
                .seatId(seatId)
                .seatNo(seatNo)
                .createdAt(passenger.getCreatedAt())
                .updatedAt(passenger.getUpdatedAt())
                .build();
    }

    public PageResponseDTO<PassengerResponseDTO> toPageResponseDTO(
            Page<Passenger> passengerPage
    ) {
        return pageMapper.toPageResponse(passengerPage, this::toBasicResponseDTO);
    }

    public void updateEntityFromRequest(Passenger passenger, PassengerRequestDTO requestDTO) {
        if (passenger == null || requestDTO == null) return;

        if (requestDTO.getFirstName() != null) {
            passenger.setFirstName(requestDTO.getFirstName());
        }
        if (requestDTO.getMiddleName() != null) {
            passenger.setMiddleName(requestDTO.getMiddleName());
        }
        if (requestDTO.getLastName() != null) {
            passenger.setLastName(requestDTO.getLastName());
        }
        if (requestDTO.getSuffix() != null) {
            passenger.setSuffix(requestDTO.getSuffix());
        }

        if (requestDTO.getDob() != null) {
            passenger.setDob(requestDTO.getDob());
        }
        if (requestDTO.getGender() != null) {
            passenger.setGender(requestDTO.getGender());
        }
        if (requestDTO.getGovtIdNo() != null) {
            passenger.setGovtIdNo(requestDTO.getGovtIdNo());
        }
        if (requestDTO.getEmail() != null) {
            passenger.setEmail(requestDTO.getEmail());
        }
        if (requestDTO.getPhone() != null) {
            passenger.setPhone(requestDTO.getPhone());
        }

        if (requestDTO.getCheckedBagCount() != null) {
            passenger.setCheckedBagCount(requestDTO.getCheckedBagCount());
        }

        if (requestDTO.getBookingId() != null) {
            Booking booking = bookingRepository.findById(requestDTO.getBookingId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Booking with id " + requestDTO.getBookingId() + " not found")
                    );
            passenger.setBooking(booking);
        }

        if (requestDTO.getSeatId() != null) {
            Seat seat = seatRepository.findById(requestDTO.getSeatId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Seat with id " + requestDTO.getSeatId() + " not found")
                    );
            passenger.setSeat(seat);
        }
    }

    public Passenger toEntity(PassengerRequestDTO request) {

//        Booking booking = bookingRepository.findById(request.getBookingId())
//                .orElseThrow(() ->
//                        new ResourceNotFoundException(
//                                "Booking with id " + request.getBookingId() + " not found")
//                );

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat with id " + request.getSeatId() + " not found")
                );
        Passenger passenger = Passenger.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .suffix(request.getSuffix())
                .dob(request.getDob())
                .gender(request.getGender())
                .govtIdNo(request.getGovtIdNo())
                .email(request.getEmail())
                .phone(request.getPhone())
                .checkedBagCount(request.getCheckedBagCount())
//                .booking(booking)
                .seat(seat)
                .build();

        seat.setPassenger(passenger);

        return passenger;
    }
}
