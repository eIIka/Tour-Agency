package ua.ellka.touragency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.ellka.touragency.dto.BookingDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.exception.ServiceException;
import ua.ellka.touragency.mapper.BookingMapper;
import ua.ellka.touragency.model.Booking;
import ua.ellka.touragency.model.Client;
import ua.ellka.touragency.model.Tour;
import ua.ellka.touragency.model.security.TourAgencyUserDetails;
import ua.ellka.touragency.repo.BookingRepo;
import ua.ellka.touragency.repo.ClientRepo;
import ua.ellka.touragency.repo.TourRepo;

import java.time.LocalDate;
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
    @Transactional
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthorizationDeniedException("User must be authenticated to create a booking.");
        }

        // 1. Шукаємо клієнта за User ID
        Client client = clientRepo.findByUserId(currentUserId)
                .orElseThrow(() -> new NotFoundServiceException("Client profile not found."));

        // 2. Шукаємо об'єкт Туру, використовуючи tourId з DTO
        Tour tour = tourRepo.findById(bookingDTO.getTourId())
                .orElseThrow(() -> new NotFoundServiceException("Tour not found with ID: " + bookingDTO.getTourId()));

        // 3. Перевірка дублікатів
        bookingRepo.findExistingBooking(bookingDTO.getTourId(), client.getId(), bookingDTO.getBookingDate())
                .ifPresent(existingBooking -> {
                    throw new ExistingServiceException("Booking already exists");
                });

        // 4. Створення моделі Booking (через мапер для простих полів)
        Booking booking = bookingMapper.bookingDTOToBooking(bookingDTO);

        // 5. ВАЖЛИВО: Присвоюємо об'єкти
        booking.setClient(client);
        booking.setTour(tour);

        // 6. Встановлення дати
        if(bookingDTO.getBookingDate() == null) {
            booking.setBookingDate(LocalDate.now());
        }

        try {
            Booking save = bookingRepo.save(booking);
            return bookingMapper.bookingToBookingDTO(save);
        } catch (DataAccessException e) {
            throw new ServiceException("Error while creating booking: " + e.getMessage());
        }
    }

    //17
    @Override
    @PreAuthorize("@accessChecker.isClientOwner(#clientId)|| hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("@accessChecker.isBookingOwner(#id)|| hasRole('ROLE_ADMIN')")
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

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof TourAgencyUserDetails userDetails) {
            return userDetails.getId();
        }
        return null;
    }

}
