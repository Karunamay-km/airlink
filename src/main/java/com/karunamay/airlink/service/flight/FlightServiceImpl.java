package com.karunamay.airlink.service.flight;

import com.karunamay.airlink.dto.flight.FlightRequestDTO;
import com.karunamay.airlink.dto.flight.FlightResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.exceptions.DuplicateResourceException;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.flight.FlightMapper;
import com.karunamay.airlink.model.flight.Flight;
import com.karunamay.airlink.repository.flight.FlightRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

        return flightMapper.toBasicResponseDTO(savedFlight);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightResponseDTO getFlightById(Long id) {
        log.info("Fetching flight by id {}", id);
        return flightMapper.toBasicResponseDTO(baseService.findByIdOrThrow(id, flightRepository));
    }

    @Override
    @Transactional(readOnly = true)
    public FlightResponseDTO getFlightByFlightNo(String flightNo) {
        log.info("Fetching flight by flight number: {}", flightNo);
        Flight flight = flightRepository.findByFlightNo(flightNo)
                .orElseThrow(() -> new ResourceNotFoundException("Flight with number " + flightNo + " not found."));
        return flightMapper.toBasicResponseDTO(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<FlightResponseDTO> getAllFlights(Pageable pageable) {
        log.debug("Fetching all flights.");

        return flightMapper.toPageResponseDTO(flightRepository.findAllFlights(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<FlightResponseDTO> getFlightsByDepartureAirportName(String airportName, Pageable pageable) {
        log.info("Fetching flights departing from airport: {}", airportName);
        Page<Flight> flightPage = flightRepository.findAllBySrcAirport_NameIgnoreCase(airportName, pageable);
        return flightMapper.toPageResponseDTO(flightPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<FlightResponseDTO> getFlightsByArrivalAirportName(String airportName, Pageable pageable) {
        log.info("Fetching flights arriving at airport: {}", airportName);
        Page<Flight> flightPage = flightRepository.findAllByDestAirport_NameIgnoreCase(airportName, pageable);
        return flightMapper.toPageResponseDTO(flightPage);
    }

    public PageResponseDTO<FlightResponseDTO> getFlightsBySearchParameters(
            Long src, Long dest, LocalDateTime departureTime, Integer seat, Pageable pageable
    ) {
        log.info("Fetching flights by parameter (source, destination, departure time and seat availability)");
        Page<Flight> flightPage = flightRepository.findAllFlightBySearchParameters(
                src, dest, departureTime, seat, pageable
        );
        return flightMapper.toPageResponseDTO(flightPage);
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

        return flightMapper.toBasicResponseDTO(updatedFlight);
    }

    @Override
    public void deleteFlight(Long id) {
        log.info("Delete request for flight id {}", id);
        flightRepository.delete(baseService.findByIdOrThrow(id, flightRepository));
        log.info("Flight deleted successfully id {}", id);
    }
}
