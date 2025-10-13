package com.karunamay.airlink.service.flight;

import com.karunamay.airlink.dto.flight.FlightRequestDTO;
import com.karunamay.airlink.dto.flight.FlightResponseDTO;
import com.karunamay.airlink.exceptions.DuplicateResourceException;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.flight.FlightMapper;
import com.karunamay.airlink.model.flight.Flight;
import com.karunamay.airlink.repository.flight.FlightRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final BaseService baseService;

    @Override
    public FlightResponseDTO createFlight(FlightRequestDTO requestDTO) {
        log.info("Creating new flight with flight number {}", requestDTO.getFlightNo());

        if (flightRepository.existsByFlightNo(requestDTO.getFlightNo())) {
            throw new DuplicateResourceException("Flight number " + requestDTO.getFlightNo() + " already exists.");
        }

        Flight flight = flightMapper.toEntity(requestDTO);

        if (flight.getSrcAirport().equals(flight.getDestAirport())) {
            throw new DuplicateResourceException("Source and Destination airports must be different.");
        }

        Flight savedFlight = flightRepository.save(flight);
        log.info("Flight created successfully with id: {}", savedFlight.getId());

        return flightMapper.toResponseDTO(savedFlight);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightResponseDTO getFlightById(Long id) {
        log.info("Fetching flight by id {}", id);
        return flightMapper.toResponseDTO(baseService.findByIdOrThrow(id, flightRepository));
    }

    @Override
    @Transactional(readOnly = true)
    public FlightResponseDTO getFlightByFlightNo(String flightNo) {
        log.info("Fetching flight by flight number: {}", flightNo);
        Flight flight = flightRepository.findByFlightNo(flightNo)
                .orElseThrow(() -> new ResourceNotFoundException("Flight with number " + flightNo + " not found."));
        return flightMapper.toResponseDTO(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightResponseDTO> getAllFlights() {
        log.debug("Fetching all flights.");
        return flightRepository.findAll().stream()
                .map(flightMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightResponseDTO> getFlightsByDepartureAirportName(String airportName) {
        log.info("Fetching flights departing from airport: {}", airportName);
        return flightRepository.findAllBySrcAirport_Name(airportName).stream()
                .map(flightMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightResponseDTO> getFlightsByArrivalAirportName(String airportName) {
        log.info("Fetching flights arriving at airport: {}", airportName);
        return flightRepository.findAllByDestAirport_Name(airportName).stream()
                .map(flightMapper::toResponseDTO)
                .toList();
    }

    @Override
    public FlightResponseDTO updateFlight(Long id, FlightRequestDTO requestDTO) {
        log.info("Update request for flight id {}", id);

        Flight flight = baseService.findByIdOrThrow(id, flightRepository);

        if (requestDTO.getFlightNo() != null &&
                !requestDTO.getFlightNo().equalsIgnoreCase(flight.getFlightNo())) {
            if (flightRepository.existsByFlightNo(requestDTO.getFlightNo())) {
                throw new DuplicateResourceException(
                        "Flight number " + requestDTO.getFlightNo() + " already exists.");
            }
        }

        flightMapper.updateEntityFromRequest(flight, requestDTO);

        if (flight.getSrcAirport().equals(flight.getDestAirport())) {
            throw new DuplicateResourceException("Source and Destination airports must be different after update.");
        }

        Flight updatedFlight = flightRepository.save(flight);
        log.info("Flight updated successfully id {}", updatedFlight.getId());

        return flightMapper.toResponseDTO(updatedFlight);
    }

    @Override
    public void deleteFlight(Long id) {
        log.info("Delete request for flight id {}", id);
        flightRepository.delete(baseService.findByIdOrThrow(id, flightRepository));
        log.info("Flight deleted successfully id {}", id);
    }
}
