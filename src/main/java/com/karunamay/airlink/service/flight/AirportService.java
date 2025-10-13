package com.karunamay.airlink.service.flight;

import com.karunamay.airlink.dto.flight.AirportRequestDTO;
import com.karunamay.airlink.dto.flight.AirportResponseDTO;

import java.util.List;

public interface AirportService {

    AirportResponseDTO createAirport(AirportRequestDTO requestDTO);

    AirportResponseDTO getAirportById(Long id);

    AirportResponseDTO getAirportByName(String name);

    AirportResponseDTO getAirportByCode(String code);

    List<AirportResponseDTO> getAirportsByCity(String city);

    List<AirportResponseDTO> getActiveAirports();

    List<AirportResponseDTO> getAllAirports();

    AirportResponseDTO updateAirport(Long id, AirportRequestDTO requestDTO);

    void deleteAirport(Long id);
}
