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

    public AirportResponseDTO toResponseDTO(Airport airline) {
        if (airline == null) return null;
        return AirportResponseDTO.builder()
                .id(airline.getId())
                .code(airline.getCode())
                .name(airline.getName())
                .city(airline.getCity())
                .country(airline.getCountry())
                .active(airline.getActive())
                .arrivingFlights(airline.getArrivingFlights()
                        .stream()
                        .map(flightMapper::toBasicResponseDTO)
                        .collect(Collectors.toSet())
                )
                .departingFlights(airline.getDepartingFlights()
                        .stream()
                        .map(flightMapper::toBasicResponseDTO)
                        .collect(Collectors.toSet())
                )
                .createdAt(airline.getCreatedAt())
                .updatedAt(airline.getUpdatedAt())
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
