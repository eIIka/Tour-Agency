package ua.ellka.touragency.service;

import ua.ellka.touragency.dto.BookingDTO;

import java.util.List;
import java.util.Map;

public interface BookingService {
    BookingDTO createBooking(BookingDTO bookingDTO);
    List<BookingDTO> getAllBookingsByClientId(Long clientId);
    List<BookingDTO> getBookingsForTourId(Long tourId);
    BookingDTO deleteBooking(Long id);
    Map<String, Long> getBookingStatisticsByMonth();
}
