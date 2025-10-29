package com.karunamay.airlink.service.booking;

import com.karunamay.airlink.dto.booking.PassengerRequestDTO;
import com.karunamay.airlink.dto.booking.PassengerResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.exceptions.BusinessException;
import com.karunamay.airlink.mapper.booking.PassengerMapper;
import com.karunamay.airlink.model.booking.Passenger;
import com.karunamay.airlink.model.flight.Seat;
import com.karunamay.airlink.repository.booking.PassengerRepository;
import com.karunamay.airlink.repository.flight.SeatRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {
    private final SeatRepository seatRepository;

    private final PassengerRepository passengerRepository;
    private final BaseService baseService;
    private final PassengerMapper passengerMapper;

    @Override
    @Transactional(readOnly = true)
    public PassengerResponseDTO getPassengerById(Long id) {
        log.info("Fetching user by id: {}", id);
        return passengerMapper.toBasicResponseDTO(baseService.findByIdOrThrow(id, passengerRepository));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<PassengerResponseDTO> getAllPassengers(Pageable pageable) {
        log.info("Fetching all user");
        return passengerMapper.toPageResponseDTO(passengerRepository.findAll(pageable));
    }

    @Override
    public PassengerResponseDTO updatePassengerById(Long id, PassengerRequestDTO requestDTO) {
        log.info("Update passenger with id {}", id);

        Passenger passenger = baseService.findByIdOrThrow(id, passengerRepository);
        Long seatId = requestDTO.getSeatId();
        Seat seat = baseService.findByIdOrThrow(seatId, seatRepository);

        if (seat.hasBooking()) {
            throw new BusinessException("Seat has already occupied, please choose a different seat.");
        }

        passengerMapper.updateEntityFromRequest(passenger, requestDTO);
        Passenger updatedPassenger = passengerRepository.save(passenger);

        log.info("Airport updated successfully id {}", updatedPassenger.getId());

        return passengerMapper.toBasicResponseDTO(updatedPassenger);
    }

    @Override
    public PassengerResponseDTO createPassenger(PassengerRequestDTO requestDTO) {
        log.info("Create new passenger");
        Long seatId = requestDTO.getSeatId();
        Seat seat = baseService.findByIdOrThrow(seatId, seatRepository);

        if (seat.hasBooking()) {
            throw new BusinessException("Seat has already occupied, please choose a different seat.");
        }

        Passenger passenger = passengerMapper.toEntity(requestDTO);
        Passenger savedPassenger = passengerRepository.save(passenger);

        log.info("Passenger created successfully id {}", savedPassenger.getId());

        return passengerMapper.toBasicResponseDTO(savedPassenger);
    }

    @Override
    public void deletePassenger(Long id) {

        log.info("Delete request for passenger id {}", id);
        passengerRepository.delete(baseService.findByIdOrThrow(id, passengerRepository));
        log.info("Airport deleted successfully id {}", id);
    }
}
