package com.karunamay.airlink.mapper.flight;

import com.karunamay.airlink.dto.flight.SeatRequestDTO;
import com.karunamay.airlink.dto.flight.SeatResponseDTO;
import com.karunamay.airlink.model.flight.Flight;
import com.karunamay.airlink.model.flight.Seat;
import com.karunamay.airlink.repository.flight.FlightRepository;
import com.karunamay.airlink.repository.flight.SeatRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SeatMapper {

    private final FlightMapper flightMapper;
    private final SeatRepository airlineRepository;
    private final FlightRepository flightRepository;
    private final BaseService baseService;

    public SeatResponseDTO toBasicResponseDTO(Seat seat) {
        if (seat == null) return null;
        return SeatResponseDTO.builder()
                .id(seat.getId())
                .seatNo(seat.getSeatNo())
                .seatClass(seat.getSeatClass())
                .available(seat.getAvailable())
                .priceModifier(seat.getPriceModifier())
                .build();

    }

    public SeatResponseDTO toResponseDTO(Seat seat) {
        if (seat == null) return null;
        return SeatResponseDTO.builder()
                .id(seat.getId())
                .flight(flightMapper.toBasicResponseDTO(seat.getFlight()))
                .seatNo(seat.getSeatNo())
                .seatClass(seat.getSeatClass())
                .available(seat.getAvailable())
                .priceModifier(seat.getPriceModifier())
                .createdAt(seat.getCreatedAt())
                .updatedAt(seat.getUpdatedAt())
                .build();

    }

    public Seat toEntity(SeatRequestDTO requestDTO) {
        if (requestDTO == null) return null;

        Flight flight = baseService.findByIdOrThrow(requestDTO.getFlightId(), flightRepository);

        return Seat.builder()
                .flight(flight)
                .seatNo(requestDTO.getSeatNo())
                .seatClass(requestDTO.getSeatClass())
                .available(requestDTO.getAvailable())
                .priceModifier(requestDTO.getPriceModifier())
                .build();
    }

    public void updateEntityFromRequest(Seat seat, SeatRequestDTO requestDTO) {
        if (seat == null || requestDTO == null) return;
        if (requestDTO.getFlightId() != null) {
            Flight flight = baseService.findByIdOrThrow(requestDTO.getFlightId(), flightRepository);
            seat.setFlight(flight);
        }
        if (requestDTO.getSeatNo() != null) {
            seat.setSeatNo(requestDTO.getSeatNo());
        }
        if (requestDTO.getSeatClass() != null) {
            seat.setSeatClass(requestDTO.getSeatClass());
        }
        if (requestDTO.getAvailable() != null) {
            seat.setAvailable(requestDTO.getAvailable());
        }
        if (requestDTO.getPriceModifier() != null) {
            seat.setPriceModifier(requestDTO.getPriceModifier());
        }
    }

}
