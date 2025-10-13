package com.karunamay.airlink.service.flight;

import com.karunamay.airlink.dto.flight.AircraftRequestDTO;
import com.karunamay.airlink.dto.flight.AircraftResponseDTO;

import java.util.List;

public interface AircraftService {

    AircraftResponseDTO createAircraft(AircraftRequestDTO requestDTO);

    AircraftResponseDTO getAircraftById(Long id);

    AircraftResponseDTO getAircraftByRegistrationNumber(String registrationNumber);

    AircraftResponseDTO getAircraftByModel(String model);

    List<AircraftResponseDTO> getAllAircrafts();

    List<AircraftResponseDTO> getActiveAircrafts();

    List<AircraftResponseDTO> getAircraftsByAirlineName(String airlineName);

    AircraftResponseDTO updateAircraft(Long id, AircraftRequestDTO requestDTO);

    void deleteAircraft(Long id);
}
