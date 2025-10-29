package com.karunamay.airlink.service.booking;

import com.karunamay.airlink.dto.flight.SeatRequestDTO;
import com.karunamay.airlink.dto.flight.SeatResponseDTO;

import java.util.List;

public interface SeatService {

    List<SeatResponseDTO> getAllByFlightNo(String flightNo);

    SeatResponseDTO getSeatByNo(String seatNo);

    List<SeatResponseDTO> getSeatByAvailableTrue();

    SeatResponseDTO createSeat(SeatRequestDTO requestDTO);

    SeatResponseDTO updateSeat(Long id, SeatRequestDTO requestDTO);

    SeatResponseDTO getSeatById(Long id);

    void deleteSeat(Long id);

    Integer getAvailableSeatCountByFlight(Long flightId);
}
