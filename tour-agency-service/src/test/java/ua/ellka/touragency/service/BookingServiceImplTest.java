package ua.ellka.touragency.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.ellka.touragency.dto.BookingDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.mapper.BookingMapper;
import ua.ellka.touragency.model.Booking;
import ua.ellka.touragency.model.Client;
import ua.ellka.touragency.model.Tour;
import ua.ellka.touragency.repo.BookingRepo;
import ua.ellka.touragency.repo.ClientRepo;
import ua.ellka.touragency.repo.TourRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {

    private BookingRepo bookingRepo;
    private ClientRepo clientRepo;
    private TourRepo tourRepo;
    private BookingService bookingService;

    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;

    private BookingDTO bookingDTO;
    private Client client;
    private Tour tour;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingRepo = mock(BookingRepo.class);
        clientRepo = mock(ClientRepo.class);
        tourRepo = mock(TourRepo.class);
        bookingService = new BookingServiceImpl(bookingMapper, bookingRepo, clientRepo, tourRepo);

        bookingDTO = new BookingDTO();
        bookingDTO.setBookingDate(LocalDate.now());
        bookingDTO.setClientId(1L);
        bookingDTO.setTourId(1L);

        client = new Client();
        client.setId(1L);

        tour = new Tour();
        tour.setId(1L);

        booking = new Booking();
        booking.setId(1L);
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setClient(client);
        booking.setTour(tour);
    }

    @Test
    void createBookingTest_success() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));
        when(clientRepo.findById(anyLong())).thenReturn(Optional.of(client));
        when(bookingRepo.save(any())).thenReturn(booking);
        when(bookingRepo.findExistingBooking(anyLong(),anyLong(),any())).thenReturn(Optional.empty());

        BookingDTO created = bookingService.createBooking(bookingDTO);

        assertNotNull(created);
        assertEquals(bookingDTO.getTourId(), created.getTourId());
        assertEquals(bookingDTO.getClientId(), created.getClientId());
        assertEquals(bookingDTO.getBookingDate(), created.getBookingDate());
    }

    @Test
    void createBookingTest_tourNotFoundException() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> bookingService.createBooking(bookingDTO));
        assertEquals("Tour not found", exception.getMessage());
    }

    @Test
    void createBookingTest_clientNotFoundException() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));
        when(clientRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> bookingService.createBooking(bookingDTO));
        assertEquals("Client not found", exception.getMessage());
    }

    @Test
    void createBookingTest_existingBookingException() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));
        when(clientRepo.findById(anyLong())).thenReturn(Optional.of(client));
        when(bookingRepo.findExistingBooking(anyLong(),anyLong(),any())).thenReturn(Optional.of(booking));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> bookingService.createBooking(bookingDTO));
        assertEquals("Booking already exists", exception.getMessage());
    }

    @Test
    void getAllBookingsByClientIdTest_success() {
        when(clientRepo.findById(anyLong())).thenReturn(Optional.of(client));
        when(bookingRepo.findBookingsByClientId(anyLong())).thenReturn(List.of(booking));

        List<BookingDTO> bookings = bookingService.getAllBookingsByClientId(1L);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        BookingDTO bookingDTOList = bookings.get(0);
        assertEquals(bookingDTOList.getBookingDate(), bookingDTO.getBookingDate());
        assertEquals(bookingDTOList.getClientId(), bookingDTO.getClientId());
        assertEquals(bookingDTOList.getTourId(), bookingDTO.getTourId());
    }

    @Test
    void getAllBookingsByClientIdTest_clientNotFoundException() {
        when(clientRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> bookingService.getAllBookingsByClientId(1L));
        assertEquals("Client not found", exception.getMessage());
    }

    @Test
    void getAllBookingsByClientIdTest_bookingsNotFoundException() {
        when(clientRepo.findById(anyLong())).thenReturn(Optional.of(client));
        when(bookingRepo.findBookingsByClientId(anyLong())).thenReturn(List.of());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> bookingService.getAllBookingsByClientId(1L));
        assertEquals("Bookings not found", exception.getMessage());
    }

    @Test
    void getBookingsForTourIdTest_success() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));
        when(bookingRepo.findBookingsByTourId(anyLong())).thenReturn(List.of(booking));

        List<BookingDTO> bookings = bookingService.getBookingsForTourId(1L);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        BookingDTO bookingDTOList = bookings.get(0);
        assertEquals(bookingDTOList.getTourId(), bookingDTO.getTourId());
        assertEquals(bookingDTOList.getClientId(), bookingDTO.getClientId());
        assertEquals(bookingDTOList.getBookingDate(), bookingDTO.getBookingDate());
    }

    @Test
    void getBookingsForTourTest_tourIdNotFoundException() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> bookingService.getBookingsForTourId(1L));
        assertEquals("Tour not found", exception.getMessage());
    }

    @Test
    void getBookingsForTourTest_bookingsNotFoundExceptionId() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));
        when(bookingRepo.findBookingsByTourId(anyLong())).thenReturn(List.of());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> bookingService.getBookingsForTourId(1L));
        assertEquals("No bookings found for the tour with id 1", exception.getMessage());
    }

    @Test
    void deleteBookingTest_success() {
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDTO deleted = bookingService.deleteBooking(1L);

        assertNotNull(deleted);
        assertEquals(bookingDTO.getTourId(), deleted.getTourId());
        assertEquals(bookingDTO.getClientId(), deleted.getClientId());
        assertEquals(bookingDTO.getBookingDate(), deleted.getBookingDate());
    }

    @Test
    void deleteBookingTest_bookingNotFoundException() {
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> bookingService.deleteBooking(1L));
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void getBookingStatisticsByMonthTest_success() {
        BookingRepo.BookingCountByMonthResult result1 = mock(BookingRepo.BookingCountByMonthResult.class);
        BookingRepo.BookingCountByMonthResult result2 = mock(BookingRepo.BookingCountByMonthResult.class);

        when(result1.getMonth()).thenReturn("JANUARY");
        when(result1.getCount()).thenReturn(10L);
        when(result2.getMonth()).thenReturn("FEBRUARY");
        when(result2.getCount()).thenReturn(20L);

        when(bookingRepo.findBookingCountsByMonth()).thenReturn(List.of(result1, result2));
        when(bookingRepo.count()).thenReturn(2L);

        Map<String, Long> statistics = bookingService.getBookingStatisticsByMonth();

        assertNotNull(statistics);
        assertEquals(10L, statistics.get("JANUARY"));
        assertEquals(20L, statistics.get("FEBRUARY"));
    }

    @Test
    void getBookingStatisticsByMonthTest_noBookings() {
        when(bookingRepo.count()).thenReturn(0L);

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> bookingService.getBookingStatisticsByMonth());
        assertEquals("No bookings found", exception.getMessage());
    }
}