package com.karunamay.airlink.service.booking;

import com.karunamay.airlink.dto.booking.BookingRequestDTO;
import com.karunamay.airlink.dto.booking.BookingResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.booking.BookingMapper;
import com.karunamay.airlink.mapper.booking.PassengerMapper;
import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.booking.BookingStatus;
import com.karunamay.airlink.model.booking.Passenger;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.repository.booking.BookingRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final PassengerMapper passengerMapper;
    private final BaseService baseService;

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingById(Long id) {
        log.info("Fetch booking for id {}", id);
        return bookingMapper.toResponseDTO(findBookingByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BookingResponseDTO> getBookingsByUser(User user, Pageable pageable) {
        log.info("Fetch booking for user {}", user.getUsername());
        Page<Booking> bookings = bookingRepository.findAllByUser(user, pageable);
        return bookingMapper.toPageResponseDTO(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingByPnrCode(String pnrCode) {
        log.info("Fetch booking with pnr {}", pnrCode);
        return bookingMapper.toResponseDTO(findBookingByPnrOrThrow(pnrCode));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BookingResponseDTO> getBookingsByStatus(
            BookingStatus status,
            Pageable pageable
    ) {
        log.info("Fetch booking with status {}", status);
        Page<Booking> bookings = bookingRepository.findAllByBookingStatus(status, pageable);
        return bookingMapper.toPageResponseDTO(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BookingResponseDTO> getAllBookings(
            Pageable pageable
    ) {
        log.info("Fetch all bookings");
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        return bookingMapper.toPageResponseDTO(bookings);
    }

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO requestDTO) {
        log.info("Create new booking");

        Booking booking = bookingMapper.toEntity(requestDTO);

        Set<Passenger> bookingPassengers = booking.getPassengers();
        bookingPassengers.forEach(passenger -> {
            booking.addPassenger(passenger);
            booking.addSeat(passenger.getSeat());
            passenger.setBooking(booking);
        });

        String pnr = UUID.randomUUID().toString().replace("-", "");
        booking.setPnrCode(pnr.substring(0, Math.min(10, pnr.length())));

        Booking savedBooking = bookingRepository.save(booking);

        log.info("Booking created successfully");

        return bookingMapper.toResponseDTO(savedBooking);
    }

    @Override
    public BookingResponseDTO updateBooking(Long id, BookingRequestDTO requestDTO) {

        log.info("Update booking with id {}", id);

        Booking booking = baseService.findByIdOrThrow(id, bookingRepository);
        bookingMapper.updateEntityFromRequest(booking, requestDTO);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking update successfully");

        return bookingMapper.toBasicResponseDTO(updatedBooking);
    }

    private Booking findBookingByIdOrThrow(Long id) {
        return bookingRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking with id " + id + " not found"
                        )
                );
    }

    private Booking findBookingByUserOrThrow(User user) {
        return bookingRepository
                .findByUserUsername(user.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking for user " + user.getUsername() + " not found"
                        )
                );
    }

    private Booking findBookingByPnrOrThrow(String pnrCode) {
        return bookingRepository
                .findByPnrCode(pnrCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking with pnr " + pnrCode + " not found"
                        )
                );
    }
}
