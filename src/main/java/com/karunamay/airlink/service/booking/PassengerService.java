package com.karunamay.airlink.service.booking;

import com.karunamay.airlink.dto.booking.PassengerRequestDTO;
import com.karunamay.airlink.dto.booking.PassengerResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import org.springframework.data.domain.Pageable;


public interface PassengerService {

    PassengerResponseDTO getPassengerById(Long id);

    PageResponseDTO<PassengerResponseDTO> getAllPassengers(Pageable pageable);

    PassengerResponseDTO updatePassengerById(Long id, PassengerRequestDTO requestDTO);

    PassengerResponseDTO createPassenger(PassengerRequestDTO requestDTO);

    void deletePassenger(Long id);
}
