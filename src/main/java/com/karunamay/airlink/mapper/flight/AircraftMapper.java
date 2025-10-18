package com.karunamay.airlink.mapper.flight;

import com.karunamay.airlink.dto.flight.AircraftRequestDTO;
import com.karunamay.airlink.dto.flight.AircraftResponseDTO;
import com.karunamay.airlink.model.flight.Aircraft;
import com.karunamay.airlink.model.flight.Airline;
import com.karunamay.airlink.repository.flight.AirlineRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AircraftMapper {

    private final AirlineRepository airlineRepository;
    private final BaseService baseService;

    public AircraftResponseDTO toBasicResponseDTO(Aircraft aircraft) {
        if (aircraft == null) return null;
        return AircraftResponseDTO.builder()
                .id(aircraft.getId())
                .model(aircraft.getModel())
                .registrationNumber(aircraft.getRegistrationNumber())
                .capacity(aircraft.getCapacity())
                .active(aircraft.getActive())
                .build();

    }

    public AircraftResponseDTO toResponseDTO(Aircraft aircraft) {
        if (aircraft == null) return null;
        return AircraftResponseDTO.builder()
                .id(aircraft.getId())
                .model(aircraft.getModel())
                .registrationNumber(aircraft.getRegistrationNumber())
                .capacity(aircraft.getCapacity())
                .active(aircraft.getActive())
                .createdAt(aircraft.getCreatedAt())
                .updatedAt(aircraft.getUpdatedAt())
                .build();

    }

    public Aircraft toEntity(AircraftRequestDTO requestDTO) {
        if (requestDTO == null) return null;

        Airline airline = baseService.findByIdOrThrow(requestDTO.getAirlineId(), airlineRepository);

        return Aircraft.builder()
                .model(requestDTO.getModel())
                .registrationNumber(requestDTO.getRegistrationNumber())
                .capacity(requestDTO.getCapacity())
                .active(requestDTO.getActive())
                .airline(airline)
                .build();
    }

    public void updateEntityFromRequest(Aircraft aircraft, AircraftRequestDTO requestDTO) {
        if (aircraft == null || requestDTO == null) return;
        if (requestDTO.getModel() != null) {
            aircraft.setModel(requestDTO.getModel());
        }
        if (requestDTO.getRegistrationNumber() != null) {
            aircraft.setRegistrationNumber(requestDTO.getRegistrationNumber());
        }
        if (requestDTO.getCapacity() != null) {
            aircraft.setCapacity(requestDTO.getCapacity());
        }
        if (requestDTO.getActive() != null) {
            aircraft.setActive(requestDTO.getActive());
        }
        if (requestDTO.getAirlineId() != null) {
            Airline airline = baseService.findByIdOrThrow(requestDTO.getAirlineId(), airlineRepository);
            aircraft.setAirline(airline);
        }
    }

}
