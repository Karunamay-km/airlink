package com.karunamay.airlink.service.flight;

import com.karunamay.airlink.dto.flight.AirlineRequestDTO;
import com.karunamay.airlink.dto.flight.AirlineResponseDTO;

import java.util.List;

public interface AirlineService {

    AirlineResponseDTO getAirlineById(Long id);

    AirlineResponseDTO getAirlineByName(String name);

    AirlineResponseDTO getAirlineByCode(String code);

    List<AirlineResponseDTO> getActiveAirlines();

    List<AirlineResponseDTO> getAllAirlines();

    AirlineResponseDTO createAirline(AirlineRequestDTO requestDTO);

    AirlineResponseDTO updateAirline(Long id, AirlineRequestDTO requestDTO);

    void deleteAirline(Long id);

}
