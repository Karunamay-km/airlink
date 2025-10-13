package com.karunamay.airlink.service.flight;

import com.karunamay.airlink.dto.flight.AirlineRequestDTO;
import com.karunamay.airlink.dto.flight.AirlineResponseDTO;
import com.karunamay.airlink.exceptions.DuplicateResourceException;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.flight.AirlineMapper;
import com.karunamay.airlink.model.flight.Airline;
import com.karunamay.airlink.repository.flight.AirlineRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AirlineServiceImpl implements AirlineService {

    private final AirlineRepository airlineRepository;
    private final AirlineMapper airlineMapper;
    private final BaseService baseService;

    @Override
    @Transactional(readOnly = true)
    public AirlineResponseDTO getAirlineById(Long id) {
        log.info("Fetching airline by id {}", id);
        Airline airline = baseService.findByIdOrThrow(id, airlineRepository);
        return airlineMapper.toResponseDTO(airline);
    }

    @Override
    @Transactional(readOnly = true)
    public AirlineResponseDTO getAirlineByName(String name) {
        log.info("Fetching airline by name: {}", name);
        Airline airline = airlineRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Airline with name " + name + " not found."));
        return airlineMapper.toResponseDTO(airline);
    }

    @Override
    @Transactional(readOnly = true)
    public AirlineResponseDTO getAirlineByCode(String code) {
        log.info("Fetching airline by code: {}", code);
        Airline airline = airlineRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("Airline with code " + code + " not found."));
        return airlineMapper.toResponseDTO(airline);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AirlineResponseDTO> getActiveAirlines() {
        log.debug("Fetching all active airlines.");
        return airlineRepository.findByActiveTrue()
                .stream()
                .map(airlineMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<AirlineResponseDTO> getAllAirlines() {
        log.debug("Fetching all airlines.");
        return airlineRepository.findAll()
                .stream()
                .map(airlineMapper::toResponseDTO)
                .toList();
    }

    @Override
    public AirlineResponseDTO createAirline(AirlineRequestDTO requestDTO) {
        log.info("Creating new airline with code {} and name {}", requestDTO.getCode(), requestDTO.getName());

        if (airlineRepository.existsByCode(requestDTO.getCode())) {
            throw new DuplicateResourceException("Airline code " + requestDTO.getCode() + " already exists.");
        }

        if (airlineRepository.existsByCode(requestDTO.getName())) {
            throw new DuplicateResourceException("Airline name " + requestDTO.getName() + " already exists.");
        }

        Airline airline = airlineMapper.toEntity(requestDTO);
        Airline savedAirline = airlineRepository.save(airline);

        log.info("Airline created successfully with id: {}", savedAirline.getId());
        return airlineMapper.toResponseDTO(savedAirline);
    }

    @Override
    public AirlineResponseDTO updateAirline(Long id, AirlineRequestDTO requestDTO) {
        log.info("Update request for airline id {}", id);

        Airline airline = baseService.findByIdOrThrow(id, airlineRepository);

        if (airline.getName().equalsIgnoreCase(requestDTO.getName())) {
            if (airlineRepository.existsByName(requestDTO.getName())) {
                throw new DuplicateResourceException(
                        "Airline name " + requestDTO.getName() + " already exists.");
            }
        }

        if (airline.getCode().equalsIgnoreCase(requestDTO.getCode())) {
            if (airlineRepository.existsByName(requestDTO.getCode())) {
                throw new DuplicateResourceException(
                        "Airline code " + requestDTO.getCode() + " already exists.");
            }
        }

        airlineMapper.updateEntityFromRequest(airline, requestDTO);
        Airline updatedAirline = airlineRepository.save(airline);

        log.info("Airline updated successfully id {}", updatedAirline.getId());

        return airlineMapper.toResponseDTO(updatedAirline);
    }

    @Override
    public void deleteAirline(Long id) {
        log.info("Delete request for airline id {}", id);
        Airline airline = baseService.findByIdOrThrow(id, airlineRepository);
        airlineRepository.delete(airline);
        log.info("Airline deleted successfully id {}", id);
    }

}
