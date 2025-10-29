package com.karunamay.airlink.service.flight;

import com.karunamay.airlink.dto.flight.AirportRequestDTO;
import com.karunamay.airlink.dto.flight.AirportResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.exceptions.DuplicateResourceException;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.flight.AirportMapper;
import com.karunamay.airlink.model.flight.Airport;
import com.karunamay.airlink.repository.flight.AirportRepository;
import com.karunamay.airlink.service.BaseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;
    private final BaseService baseService;

    @Override
    public AirportResponseDTO createAirport(AirportRequestDTO requestDTO) {
        log.info(
            "Creating new airline with code {} and name {}",
            requestDTO.getCode(),
            requestDTO.getName()
        );

        if (airportRepository.existsByCode(requestDTO.getCode())) {
            throw new DuplicateResourceException(
                "Airport code " + requestDTO.getCode() + " already exists."
            );
        }

        if (airportRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateResourceException(
                "Airport name " + requestDTO.getName() + " already exists."
            );
        }

        Airport airport = airportMapper.toEntity(requestDTO);
        Airport savedAirport = airportRepository.save(airport);

        log.info(
            "Airport created successfully with id: {}",
            savedAirport.getId()
        );
        return airportMapper.toBasicResponseDTO(savedAirport);
    }

    @Override
    @Transactional(readOnly = true)
    public AirportResponseDTO getAirportById(Long id) {
        log.info("Fetching airport by id {}", id);
        return airportMapper.toBasicResponseDTO(
            baseService.findByIdOrThrow(id, airportRepository)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AirportResponseDTO getAirportByName(String name) {
        log.info("Fetching airport by name: {}", name);
        Airport airport = airportRepository
            .findByNameIgnoreCase(name)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Airport with name " + name + " not found."
                )
            );
        return airportMapper.toBasicResponseDTO(airport);
    }

    @Override
    @Transactional(readOnly = true)
    public AirportResponseDTO getAirportByCode(String code) {
        log.info("Fetching airport by code: {}", code);
        Airport airport = airportRepository
            .findByCodeIgnoreCase(code)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Airport with code " + code + " not found."
                )
            );
        return airportMapper.toBasicResponseDTO(airport);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AirportResponseDTO> getAirportsByCity(
        String city,
        Pageable pageable
    ) {
        log.info("Fetching airports by city: {}", city);
        return airportMapper.toPageResponseDTO(
            airportRepository.findByCityIgnoreCase(city, pageable)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AirportResponseDTO> getActiveAirports(
        Pageable pageable
    ) {
        log.debug("Fetching all active airports.");
        return airportMapper.toPageResponseDTO(
            airportRepository.findByActiveTrue(pageable)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AirportResponseDTO> getAllAirports(
        Pageable pageable
    ) {
        log.debug("Fetching all airports.");
        return airportMapper.toPageResponseDTO(
            airportRepository.findAll(pageable)
        );
    }

    @Override
    public AirportResponseDTO updateAirport(
        Long id,
        AirportRequestDTO requestDTO
    ) {
        log.info("Update request for airport id {}", id);

        Airport airport = baseService.findByIdOrThrow(id, airportRepository);

        if (!airport.getName().equalsIgnoreCase(requestDTO.getName())) {
            if (airportRepository.existsByName(requestDTO.getName())) {
                throw new DuplicateResourceException(
                    "Airport name " + requestDTO.getName() + " already exists."
                );
            }
        }

        if (!airport.getCode().equalsIgnoreCase(requestDTO.getCode())) {
            if (airportRepository.existsByName(requestDTO.getCode())) {
                throw new DuplicateResourceException(
                    "Airport code " + requestDTO.getCode() + " already exists."
                );
            }
        }

        airportMapper.updateEntityFromRequest(airport, requestDTO);
        Airport updatedAirport = airportRepository.save(airport);

        log.info("Airport updated successfully id {}", updatedAirport.getId());

        return airportMapper.toBasicResponseDTO(updatedAirport);
    }

    @Override
    public void deleteAirport(Long id) {
        log.info("Delete request for airport id {}", id);
        airportRepository.delete(
            baseService.findByIdOrThrow(id, airportRepository)
        );
        log.info("Airport deleted successfully id {}", id);
    }
}
