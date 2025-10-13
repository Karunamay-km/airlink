package com.karunamay.airlink.service.flight;

import com.karunamay.airlink.dto.flight.FlightRequestDTO;
import com.karunamay.airlink.dto.flight.FlightResponseDTO;

import java.util.List;

public interface FlightService {

    FlightResponseDTO createFlight(FlightRequestDTO requestDTO);

    FlightResponseDTO getFlightById(Long id);

    FlightResponseDTO getFlightByFlightNo(String flightNo);

    List<FlightResponseDTO> getAllFlights();

    List<FlightResponseDTO> getFlightsByDepartureAirportName(String airportName);

    List<FlightResponseDTO> getFlightsByArrivalAirportName(String airportName);

    FlightResponseDTO updateFlight(Long id, FlightRequestDTO requestDTO);

    void deleteFlight(Long id);
}
