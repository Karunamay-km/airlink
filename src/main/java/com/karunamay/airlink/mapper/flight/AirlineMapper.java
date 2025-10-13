package com.karunamay.airlink.mapper.flight;

import com.karunamay.airlink.dto.flight.AirlineRequestDTO;
import com.karunamay.airlink.dto.flight.AirlineResponseDTO;
import com.karunamay.airlink.dto.permission.PermissionUpdateRequestDTO;
import com.karunamay.airlink.model.flight.Airline;
import com.karunamay.airlink.model.user.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AirlineMapper {

    public AirlineResponseDTO toResponseDTO(Airline airline) {
        if (airline == null) return null;
        return AirlineResponseDTO.builder()
                .id(airline.getId())
                .code(airline.getCode())
                .name(airline.getName())
                .country(airline.getCountry())
                .logoUrl(airline.getLogoUrl())
                .active(airline.getActive())
                .createdAt(airline.getCreatedAt())
                .updatedAt(airline.getUpdatedAt())
                .build();
    }

    public Airline toEntity(AirlineRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        return Airline.builder()
                .code(requestDTO.getCode().toUpperCase())
                .name(requestDTO.getName())
                .logoUrl(requestDTO.getLogoUrl())
                .active(requestDTO.getActive())
                .build();
    }

    public void updateEntityFromRequest(Airline airline, AirlineRequestDTO requestDTO) {
        if (airline == null || requestDTO == null) return;
        if (requestDTO.getCode() != null) {
            airline.setCode(requestDTO.getCode());
        }
        if (requestDTO.getName() != null) {
            airline.setName(requestDTO.getName());
        }
        if (requestDTO.getLogoUrl() != null) {
            airline.setLogoUrl(requestDTO.getLogoUrl());
        }
        if (requestDTO.getActive() != null) {
            airline.setActive(requestDTO.getActive());
        }
    }
}
