package com.karunamay.airlink.service.booking;

import com.karunamay.airlink.dto.flight.SeatRequestDTO;
import com.karunamay.airlink.dto.flight.SeatResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.exceptions.DuplicateResourceException;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.flight.SeatMapper;
import com.karunamay.airlink.model.flight.Seat;
import com.karunamay.airlink.repository.flight.FlightRepository;
import com.karunamay.airlink.repository.flight.SeatRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    public PageResponseDTO<SeatResponseDTO> getAllByFlightNo(String flightNo, Pageable pageable) {
        log.info("Fetching paginated seats for flight number: {} with pageable: {}", flightNo, pageable);

        Page<Seat> seatPage = seatRepository.findByFlight_FlightNo(flightNo, pageable);

        return seatMapper.toPageResponseDTO(seatPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<SeatResponseDTO> getSeatByAvailableTrue(Pageable pageable) {
        log.info("Fetching paginated available seats with pageable: {}", pageable);

        Page<Seat> seatPage = seatRepository.findByAvailableTrue(pageable);

        return seatMapper.toPageResponseDTO(seatPage);
    }


    @Override
    @Transactional(readOnly = true)
    public SeatResponseDTO getSeatByNo(String seatNo) {
        log.info("Fetching seat by seat number: {}", seatNo);

        Seat seat = seatRepository.findBySeatNo(seatNo)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with number: " + seatNo));

        return seatMapper.toResponseDTO(seat);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatResponseDTO getSeatById(Long id) {
        log.info("Fetching seat by id {}", id);
        Seat seat = baseService.findByIdOrThrow(id, seatRepository);
        return seatMapper.toResponseDTO(seat);
    }

    @Override
    public SeatResponseDTO createSeat(SeatRequestDTO requestDTO) {
        log.info("Create request for new seat with seatNo {} on flight ID {}",
                requestDTO.getSeatNo(), requestDTO.getFlightId());

        checkDuplicateSeatOnSameFlight(requestDTO.getFlightId(), requestDTO.getSeatNo());

        Seat seat = seatMapper.toEntity(requestDTO);
        Seat savedSeat = seatRepository.save(seat);

        log.info("Seat created successfully id {}", savedSeat.getId());
        return seatMapper.toResponseDTO(savedSeat);
    }

    @Override
    public SeatResponseDTO updateSeat(Long id, SeatRequestDTO requestDTO) {
        log.info("Update request for seat id {}", id);

        Seat seat = baseService.findByIdOrThrow(id, seatRepository);

        if (requestDTO.getSeatNo() != null &&
                !requestDTO.getSeatNo().equalsIgnoreCase(seat.getSeatNo())) {
            checkDuplicateSeatOnSameFlight(seat.getFlight().getId(), requestDTO.getSeatNo());
        }

        seatMapper.updateEntityFromRequest(seat, requestDTO);

        Seat updatedSeat = seatRepository.save(seat);
        log.info("Seat updated successfully id {}", updatedSeat.getId());

        return seatMapper.toResponseDTO(updatedSeat);
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

        if (!flightRepository.existsById(flightId)) {
            throw new ResourceNotFoundException("Flight not found with ID: " + flightId);
        }

        return seatRepository.availableSeatCountByFlight(flightId);
    }


    private void checkDuplicateSeatOnSameFlight(Long flightId, String seatNo) {
        if (seatRepository.existsByFlight_IdAndSeatNo(flightId, seatNo)) {
            throw new DuplicateResourceException(
                    String.format("Seat no '%s' already exists on flight Id %d", seatNo, flightId)
            );
        }
    }

}
