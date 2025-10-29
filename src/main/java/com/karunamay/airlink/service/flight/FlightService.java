package com.karunamay.airlink.service.flight;

import com.karunamay.airlink.dto.flight.FlightRequestDTO;
import com.karunamay.airlink.dto.flight.FlightResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightService {

    FlightResponseDTO createFlight(FlightRequestDTO requestDTO);

    FlightResponseDTO getFlightById(Long id);

    FlightResponseDTO getFlightByFlightNo(String flightNo);

    PageResponseDTO<FlightResponseDTO> getAllFlights(Pageable pageable);

    PageResponseDTO<FlightResponseDTO> getFlightsByDepartureAirportName(String airportName, Pageable pageable);

    PageResponseDTO<FlightResponseDTO> getFlightsByArrivalAirportName(String airportName, Pageable pageable);

    PageResponseDTO<FlightResponseDTO> getFlightsBySearchParameters(
            Long srcAirportId,
            Long destAirportId,
            LocalDateTime departureTime,
            Integer seats,
            Pageable pageable
    );

    FlightResponseDTO updateFlight(Long id, FlightRequestDTO requestDTO);

    void deleteFlight(Long id);
}
