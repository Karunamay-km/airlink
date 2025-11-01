package com.karunamay.airlink.service.booking;

import com.karunamay.airlink.dto.flight.SeatRequestDTO;
import com.karunamay.airlink.dto.flight.SeatResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface SeatService {

    PageResponseDTO<SeatResponseDTO> getAllByFlightNo(String flightNo, Pageable pageable);

    SeatResponseDTO getSeatByNo(String seatNo);

    PageResponseDTO<SeatResponseDTO> getSeatByAvailableTrue(Pageable pageable);

    SeatResponseDTO createSeat(SeatRequestDTO requestDTO);

    SeatResponseDTO updateSeat(Long id, SeatRequestDTO requestDTO);

    SeatResponseDTO getSeatById(Long id);

    void deleteSeat(Long id);

    Integer getAvailableSeatCountByFlight(Long flightId);
}
