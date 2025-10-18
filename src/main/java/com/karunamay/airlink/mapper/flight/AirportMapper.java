package com.karunamay.airlink.mapper.flight;

import com.karunamay.airlink.dto.flight.AirportRequestDTO;
import com.karunamay.airlink.dto.flight.AirportResponseDTO;
import com.karunamay.airlink.model.flight.Airport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AirportMapper {

    private final FlightMapper flightMapper;

    public AirportResponseDTO toBasicResponseDTO(Airport airport) {
        if (airport == null) return null;
        return AirportResponseDTO.builder()
                .id(airport.getId())
                .code(airport.getCode())
                .name(airport.getName())
                .city(airport.getCity())
                .country(airport.getCountry())
                .active(airport.getActive())
                .build();
    }

    public AirportResponseDTO toResponseDTO(Airport airport) {
        if (airport == null) return null;
        return AirportResponseDTO.builder()
                .id(airport.getId())
                .code(airport.getCode())
                .name(airport.getName())
                .city(airport.getCity())
                .country(airport.getCountry())
                .active(airport.getActive())
                .createdAt(airport.getCreatedAt())
                .updatedAt(airport.getUpdatedAt())
                .build();
    }

    public Airport toEntity(AirportRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        return Airport.builder()
                .code(requestDTO.getCode().toUpperCase())
                .name(requestDTO.getName())
                .city(requestDTO.getCity())
                .active(requestDTO.getActive())
                .build();
    }

    public void updateEntityFromRequest(Airport airport, AirportRequestDTO requestDTO) {
        if (airport == null || requestDTO == null) return;
        if (requestDTO.getCode() != null) {
            airport.setCode(requestDTO.getCode());
        }
        if (requestDTO.getName() != null) {
            airport.setName(requestDTO.getName());
        }
        if (requestDTO.getCity() != null) {
            airport.setCity(requestDTO.getCity());
        }
        if (requestDTO.getActive() != null) {
            airport.setActive(requestDTO.getActive());
        }
    }
}
