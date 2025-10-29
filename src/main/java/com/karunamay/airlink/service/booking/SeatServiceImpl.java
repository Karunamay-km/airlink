package com.karunamay.airlink.service.booking;


import com.karunamay.airlink.dto.flight.SeatRequestDTO;
import com.karunamay.airlink.dto.flight.SeatResponseDTO;
import com.karunamay.airlink.exceptions.DuplicateResourceException;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.flight.SeatMapper;
import com.karunamay.airlink.model.flight.Seat;
import com.karunamay.airlink.repository.flight.FlightRepository;
import com.karunamay.airlink.repository.flight.SeatRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
    private final FlightRepository flightRepository;

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;
    private final BaseService baseService;


    @Override
    @Transactional(readOnly = true)
    public List<SeatResponseDTO> getAllByFlightNo(String flightNo) {
        log.info("Fetching all seats for flight number: {}", flightNo);
        return seatRepository.findByFlight_FlightNo(flightNo).stream()
                .map(seatMapper::toBasicResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SeatResponseDTO getSeatByNo(String seatNo) {
        log.info("Fetching seat by seat number: {}", seatNo);
        return seatMapper.toResponseDTO(
                seatRepository
                        .findBySeatNo(seatNo)
                        .orElseThrow(() -> new ResourceNotFoundException("Seat with no " + seatNo + " not found"))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponseDTO> getSeatByAvailableTrue() {
        log.info("Fetching all available seats.");
        return seatRepository.findByAvailableTrue().stream().map(seatMapper::toBasicResponseDTO).toList();
    }

    @Override
    public SeatResponseDTO createSeat(SeatRequestDTO requestDTO) {

        log.info("Creating new seat with seatNo {} for flightId {}", requestDTO.getSeatNo(), requestDTO.getFlightId());

        if (seatRepository.existsByFlight_IdAndSeatNo(requestDTO.getFlightId(), requestDTO.getSeatNo())) {
            throw new DuplicateResourceException(
                    String.format("Seat no '%s' already exists on flight Id %d", requestDTO.getSeatNo(), requestDTO.getFlightId())
            );
        }

        Seat seat = seatMapper.toEntity(requestDTO);
        Seat savedSeat = seatRepository.save(seat);

        log.info("Seat created successfully with id: {}", savedSeat.getId());

        return seatMapper.toBasicResponseDTO(savedSeat);
    }

    @Override
    public SeatResponseDTO updateSeat(Long id, SeatRequestDTO requestDTO) {
        log.info("Update request for seat id {}", id);

        Seat seat = baseService.findByIdOrThrow(id, seatRepository);
        Long newFlightId = requestDTO.getFlightId() != null ? requestDTO.getFlightId() : seat.getFlight().getId();
        String newSeatNo = requestDTO.getSeatNo() != null ? requestDTO.getSeatNo() : seat.getSeatNo();

        if (!newFlightId.equals(seat.getFlight().getId()) || !newSeatNo.equalsIgnoreCase(seat.getSeatNo())) {
            checkDuplicateSeatOnSameFlight(requestDTO.getFlightId(), requestDTO.getSeatNo());
        }

        seatMapper.updateEntityFromRequest(seat, requestDTO);

        Seat updatedSeat = seatRepository.save(seat);

        log.info("Seat updated successfully id {}", updatedSeat.getId());

        return seatMapper.toResponseDTO(updatedSeat);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatResponseDTO getSeatById(Long id) {
        log.info("Fetching seat by id {}", id);
        Seat seat = baseService.findByIdOrThrow(id, seatRepository);
        return seatMapper.toResponseDTO(seat);
    }

    @Override
    public void deleteSeat(Long id) {
        log.info("Delete request for seat id {}", id);

        Seat seat = baseService.findByIdOrThrow(id, seatRepository);

        if (seat.hasBooking()) {
            throw new DuplicateResourceException("Cannot delete seat id " + id + ": It has associated bookings.");
        }

        seatRepository.delete(seat);
        log.info("Seat deleted successfully id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableSeatCountByFlight(Long flightId) {
        log.info("Fetching available seat count for flight ID: {}", flightId);
        boolean isFlightExists = flightRepository.existsById(flightId);
        return isFlightExists ? seatRepository.availableSeatCountByFlight(flightId) : 0;
    }


    private void checkDuplicateSeatOnSameFlight(Long flightId, String seatNo) {
        if (seatRepository.existsByFlight_IdAndSeatNo(flightId, seatNo)) {
            throw new DuplicateResourceException(
                    String.format("Seat no '%s' already exists on flight Id %d", seatNo, flightId)
            );
        }
    }

}
