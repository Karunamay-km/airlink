package com.karunamay.airlink.mapper.flight;

import com.karunamay.airlink.dto.flight.FlightRequestDTO;
import com.karunamay.airlink.dto.flight.FlightResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.mapper.PageMapper;
import com.karunamay.airlink.mapper.booking.BookingMapper;
import com.karunamay.airlink.model.flight.Aircraft;
import com.karunamay.airlink.model.flight.Airline;
import com.karunamay.airlink.model.flight.Airport;
import com.karunamay.airlink.model.flight.Flight;
import com.karunamay.airlink.repository.flight.AircraftRepository;
import com.karunamay.airlink.repository.flight.AirlineRepository;
import com.karunamay.airlink.repository.flight.AirportRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FlightMapper {

    private final BaseService baseService;
    private final AirlineMapper airlineMapper;
    private final AircraftMapper aircraftMapper;
    private final AirportMapper airportMapper;
    private final PageMapper pageMapper;
    private final AirlineRepository airlineRepository;
    private final AircraftRepository aircraftRepository;
    private final AirportRepository airportRepository;

    public FlightResponseDTO toBasicResponseDTO(Flight flight) {
        if (flight == null) return null;
        return FlightResponseDTO.builder()
                .id(flight.getId())
                .airline(airlineMapper.toBasicResponseDTO(flight.getAirline()))
                .aircraft(aircraftMapper.toBasicResponseDTO(flight.getAircraft()))
                .srcAirport(airportMapper.toBasicResponseDTO(flight.getSrcAirport()))
                .destAirport(airportMapper.toBasicResponseDTO(flight.getDestAirport()))
                .flightNo(flight.getFlightNo())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .basePrice(flight.getBasePrice())
                .build();
    }

    public FlightResponseDTO toResponseDTO(Flight flight) {
        if (flight == null) return null;
        return FlightResponseDTO.builder()
                .id(flight.getId())
                .airline(airlineMapper.toResponseDTO(flight.getAirline()))
                .aircraft(aircraftMapper.toResponseDTO(flight.getAircraft()))
                .srcAirport(airportMapper.toBasicResponseDTO(flight.getSrcAirport()))
                .destAirport(airportMapper.toBasicResponseDTO(flight.getDestAirport()))
                .flightNo(flight.getFlightNo())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .basePrice(flight.getBasePrice())
                .status(flight.getStatus())
                .createdAt(flight.getCreatedAt())
                .updatedAt(flight.getUpdatedAt())
                .build();
    }

    public PageResponseDTO<FlightResponseDTO> toPageResponseDTO(Page<Flight> flightPage) {
        return pageMapper.toPageResponse(flightPage, this::toBasicResponseDTO);
    }

    public Flight toEntity(FlightRequestDTO requestDTO) {


        Airline airline = baseService.findByIdOrThrow(requestDTO.getAirlineId(), airlineRepository);
        Aircraft aircraft = baseService.findByIdOrThrow(requestDTO.getAircraftId(), aircraftRepository);
        Airport srcAirport = baseService.findByIdOrThrow(requestDTO.getSrcAirportId(), airportRepository);
        Airport destAirport = baseService.findByIdOrThrow(requestDTO.getDestAirportId(), airportRepository);

        return Flight.builder()
                .airline(airline)
                .aircraft(aircraft)
                .srcAirport(srcAirport)
                .destAirport(destAirport)
                .flightNo(requestDTO.getFlightNo())
                .departureTime(requestDTO.getDepartureTime())
                .arrivalTime(requestDTO.getArrivalTime())
                .basePrice(requestDTO.getBasePrice())
                .build();
    }

    public void updateEntityFromRequest(Flight flight, FlightRequestDTO requestDTO) {
        if (flight == null || requestDTO == null) return;
        if (requestDTO.getAirlineId() != null) {
            Airline airline = baseService.findByIdOrThrow(requestDTO.getAirlineId(), airlineRepository);
            flight.setAirline(airline);
        }
        if (requestDTO.getAircraftId() != null) {
            Aircraft aircraft = baseService.findByIdOrThrow(requestDTO.getAircraftId(), aircraftRepository);
            flight.setAircraft(aircraft);
        }
        if (requestDTO.getSrcAirportId() != null) {
            Airport airport = baseService.findByIdOrThrow(requestDTO.getSrcAirportId(), airportRepository);
            flight.setSrcAirport(airport);
        }
        if (requestDTO.getDestAirportId() != null) {
            Airport airport = baseService.findByIdOrThrow(requestDTO.getDestAirportId(), airportRepository);
            flight.setDestAirport(airport);
        }
        if (requestDTO.getDestAirportId() != null) {
            Airport airport = baseService.findByIdOrThrow(requestDTO.getDestAirportId(), airportRepository);
            flight.setDestAirport(airport);
        }
        if (requestDTO.getFlightNo() != null) {
            flight.setFlightNo(requestDTO.getFlightNo());
        }
        if (requestDTO.getArrivalTime() != null) {
            flight.setArrivalTime(requestDTO.getArrivalTime());
        }
        if (requestDTO.getDepartureTime() != null) {
            flight.setDepartureTime(requestDTO.getDepartureTime());
        }
        if (requestDTO.getBasePrice() != null) {
            flight.setBasePrice(requestDTO.getBasePrice());
        }
    }
}
