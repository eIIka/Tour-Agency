package ua.ellka.touragency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ua.ellka.touragency.dto.BookingDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.exception.ServiceException;
import ua.ellka.touragency.mapper.BookingMapper;
import ua.ellka.touragency.model.Booking;
import ua.ellka.touragency.model.Client;
import ua.ellka.touragency.model.Tour;
import ua.ellka.touragency.repo.BookingRepo;
import ua.ellka.touragency.repo.ClientRepo;
import ua.ellka.touragency.repo.TourRepo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final BookingRepo bookingRepo;
    private final ClientRepo clientRepo;
    private final TourRepo tourRepo;

    //16
    @Override
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        Booking booking = bookingMapper.bookingDTOToBooking(bookingDTO);

        Tour tour = tourRepo.findById(booking.getTour().getId())
                .orElseThrow(() -> new NotFoundServiceException("Tour not found"));

        Client client = clientRepo.findById(bookingDTO.getClientId())
                .orElseThrow(() -> new NotFoundServiceException("Client not found"));

        booking.setClient(client);
        booking.setTour(tour);

        bookingRepo.findExistingBooking(bookingDTO.getTourId(), bookingDTO.getClientId(), bookingDTO.getBookingDate())
                .ifPresent(existingBooking -> {
                    throw new ExistingServiceException("Booking already exists");
                });
        try {
            Booking save = bookingRepo.save(booking);
            return bookingMapper.bookingToBookingDTO(save);
        } catch (DataAccessException e) {
            throw new ServiceException("Error while creating booking: " + e.getMessage());
        }
    }

    //17
    @Override
    //@PreAuthorize("@accessChecker.isClientOwner(#clientId)")
    public List<BookingDTO> getAllBookingsByClientId(Long clientId) {
        clientRepo.findById(clientId)
                .orElseThrow(() -> new NotFoundServiceException("Client not found"));

        List<Booking> bookingsByClientId = bookingRepo.findBookingsByClientId(clientId);
        if (bookingsByClientId.isEmpty()) {
            throw new NotFoundServiceException("Bookings not found");
        }

        return bookingsByClientId.stream()
                .map(bookingMapper::bookingToBookingDTO)
                .toList();

    }

    //18
    @Override
    public List<BookingDTO> getBookingsForTourId(Long tourId) {
        tourRepo.findById(tourId)
                .orElseThrow(() -> new NotFoundServiceException("Tour not found"));

        List<Booking> bookingsByTourId = bookingRepo.findBookingsByTourId(tourId);
        if (bookingsByTourId.isEmpty()) {
            throw new NotFoundServiceException("No bookings found for the tour with id " + tourId);
        }

        return bookingsByTourId.stream()
                .map(bookingMapper::bookingToBookingDTO)
                .toList();
    }

    //19
    @Override
    //@PreAuthorize("@accessChecker.isBookingOwner(#id)")
    public BookingDTO deleteBooking(Long id) {
        Booking existingBooking = bookingRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Booking not found"));

        try {
            bookingRepo.delete(existingBooking);
            return bookingMapper.bookingToBookingDTO(existingBooking);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to delete booking due to database error: " + e.getMessage());
        }
    }

    //23
    @Override
    public Map<String, Long> getBookingStatisticsByMonth() {
        if (bookingRepo.count() == 0) {
            throw new NotFoundServiceException("No bookings found");
        }

        return bookingRepo.findBookingCountsByMonth().stream()
                .collect(Collectors.toMap(
                        BookingRepo.BookingCountByMonthResult::getMonth,
                        BookingRepo.BookingCountByMonthResult::getCount
                ));
    }

}
