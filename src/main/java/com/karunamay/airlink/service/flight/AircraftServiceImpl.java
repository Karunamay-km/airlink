package com.karunamay.airlink.service.flight;

import com.karunamay.airlink.dto.flight.AircraftRequestDTO;
import com.karunamay.airlink.dto.flight.AircraftResponseDTO;
import com.karunamay.airlink.exceptions.DuplicateResourceException;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.flight.AircraftMapper;
import com.karunamay.airlink.model.flight.Aircraft;
import com.karunamay.airlink.repository.flight.AircraftRepository;
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
public class AircraftServiceImpl implements AircraftService {

    private final AircraftRepository aircraftRepository;
    private final AircraftMapper aircraftMapper;
    private final BaseService baseService;

    @Override
    public AircraftResponseDTO createAircraft(AircraftRequestDTO requestDTO) {
        log.info("Creating new aircraft with registration number {}", requestDTO.getRegistrationNumber());

        if (aircraftRepository.existsByRegistrationNumber(requestDTO.getRegistrationNumber())) {
            throw new DuplicateResourceException(
                    "Aircraft registration number " + requestDTO.getRegistrationNumber() + " already exists.");
        }

        if (aircraftRepository.existsByModel(requestDTO.getModel())) {
            throw new DuplicateResourceException(
                    "Aircraft model " + requestDTO.getModel() + " already exists.");
        }

        Aircraft aircraft = aircraftMapper.toEntity(requestDTO);
        Aircraft savedAircraft = aircraftRepository.save(aircraft);

        log.info("Aircraft created successfully with id: {}", savedAircraft.getId());

        return aircraftMapper.toResponseDTO(savedAircraft);
    }

    @Override
    @Transactional(readOnly = true)
    public AircraftResponseDTO getAircraftById(Long id) {
        log.info("Fetching aircraft by id {}", id);
        return aircraftMapper.toResponseDTO(baseService.findByIdOrThrow(id, aircraftRepository));
    }

    @Override
    @Transactional(readOnly = true)
    public AircraftResponseDTO getAircraftByRegistrationNumber(String registrationNumber) {
        log.info("Fetching aircraft by registration number: {}", registrationNumber);
        Aircraft aircraft = aircraftRepository.findByRegistrationNumberIgnoreCase(registrationNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aircraft with registration number " + registrationNumber + " not found."));
        return aircraftMapper.toResponseDTO(aircraft);
    }

    @Override
    @Transactional(readOnly = true)
    public AircraftResponseDTO getAircraftByModel(String model) {
        log.info("Fetching aircraft by model: {}", model);
        Aircraft aircraft = aircraftRepository.findByModelIgnoreCase(model)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft with model " + model + " not found."));
        return aircraftMapper.toResponseDTO(aircraft);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AircraftResponseDTO> getAllAircrafts() {
        log.debug("Fetching all aircrafts.");
        return aircraftRepository.findAll().stream()
                .map(aircraftMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AircraftResponseDTO> getActiveAircrafts() {
        log.debug("Fetching all active aircrafts.");
        return aircraftRepository.findByActiveTrue().stream()
                .map(aircraftMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AircraftResponseDTO> getAircraftsByAirlineName(String airlineName) {
        log.info("Fetching aircrafts for airline: {}", airlineName);
        return aircraftRepository.findAllByAirline_NameIgnoreCase(airlineName).stream()
                .map(aircraftMapper::toResponseDTO)
                .toList();
    }

    @Override
    public AircraftResponseDTO updateAircraft(Long id, AircraftRequestDTO requestDTO) {
        log.info("Update request for aircraft id {}", id);

        Aircraft aircraft = baseService.findByIdOrThrow(id, aircraftRepository);

        if (requestDTO.getRegistrationNumber() != null &&
                !requestDTO.getRegistrationNumber().equalsIgnoreCase(aircraft.getRegistrationNumber())) {
            if (aircraftRepository.existsByRegistrationNumber(requestDTO.getRegistrationNumber())) {
                throw new DuplicateResourceException(
                        "Aircraft registration number " + requestDTO.getRegistrationNumber() + " already exists.");
            }
        }

        if (requestDTO.getModel() != null &&
                !requestDTO.getModel().equalsIgnoreCase(aircraft.getModel())) {
            if (aircraftRepository.existsByRegistrationNumber(requestDTO.getModel())) {
                throw new DuplicateResourceException(
                        "Aircraft model " + requestDTO.getModel() + " already exists.");
            }
        }

        aircraftMapper.updateEntityFromRequest(aircraft, requestDTO);
        Aircraft updatedAircraft = aircraftRepository.save(aircraft);

        log.info("Aircraft updated successfully id {}", updatedAircraft.getId());

        return aircraftMapper.toResponseDTO(updatedAircraft);
    }

    @Override
    public void deleteAircraft(Long id) {
        log.info("Delete request for aircraft id {}", id);
        Aircraft aircraft = baseService.findByIdOrThrow(id, aircraftRepository);
        if (!aircraft.getFlights().isEmpty()) {
            throw new DuplicateResourceException("Cannot delete aircraft id " + id + ": It has associated flights.");
        }
        aircraftRepository.delete(aircraft);
        log.info("Aircraft deleted successfully id {}", id);
    }
}
