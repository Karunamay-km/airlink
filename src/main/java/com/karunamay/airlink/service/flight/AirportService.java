package com.karunamay.airlink.service.flight;

import com.karunamay.airlink.dto.flight.AirportRequestDTO;
import com.karunamay.airlink.dto.flight.AirportResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface AirportService {
    AirportResponseDTO createAirport(AirportRequestDTO requestDTO);

    AirportResponseDTO getAirportById(Long id);

    AirportResponseDTO getAirportByName(String name);

    AirportResponseDTO getAirportByCode(String code);

    PageResponseDTO<AirportResponseDTO> getAirportsByCity(
        String city,
        Pageable pageable
    );

    PageResponseDTO<AirportResponseDTO> getActiveAirports(Pageable pageable);

    PageResponseDTO<AirportResponseDTO> getAllAirports(Pageable pageable);

    AirportResponseDTO updateAirport(Long id, AirportRequestDTO requestDTO);

    void deleteAirport(Long id);
}
