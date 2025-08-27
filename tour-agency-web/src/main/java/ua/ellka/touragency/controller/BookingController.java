package ua.ellka.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.ellka.touragency.dto.BookingDTO;
import ua.ellka.touragency.service.BookingService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/booking")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<BookingDTO>> getAllBookingsByClientId(@PathVariable Long clientId) {
        List<BookingDTO> allBookingsByClientId = bookingService.getAllBookingsByClientId(clientId);

        return ResponseEntity.ok(allBookingsByClientId);
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<BookingDTO>> getBookingsForTourId(@PathVariable Long tourId) {
        List<BookingDTO> bookingsForTour = bookingService.getBookingsForTourId(tourId);

        return ResponseEntity.ok(bookingsForTour);
    }

    @GetMapping("/statisticsByMonth")
    public ResponseEntity<Map<String, Long>> getBookingStatisticsByMonth() {
        Map<String, Long> bookingStatisticsByMonth = bookingService.getBookingStatisticsByMonth();

        return ResponseEntity.ok(bookingStatisticsByMonth);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        BookingDTO createBooking = bookingService.createBooking(bookingDTO);

        return ResponseEntity.ok(createBooking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookingDTO> deleteBooking(@PathVariable Long id) {
        BookingDTO deleteBooking = bookingService.deleteBooking(id);

        return ResponseEntity.ok(deleteBooking);
    }
}
