package ua.ellka.touragency.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ua.ellka.touragency.dto.BookingDTO;
import ua.ellka.touragency.model.Booking;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {
    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        bookingMapper = Mappers.getMapper(BookingMapper.class);
    }

    @Test
    void bookingToBookingDTO() {
        BookingDTO bookingDTO = bookingMapper.bookingToBookingDTO(TestData.BOOKING);

        assertNotNull(bookingDTO);
        assertEquals(TestData.BOOKING.getId(), bookingDTO.getId());
        assertEquals(TestData.BOOKING.getTour().getId(), bookingDTO.getTourId());
        assertEquals(TestData.BOOKING.getClient().getId(), bookingDTO.getClientId());
        assertEquals(TestData.BOOKING.getBookingDate(), bookingDTO.getBookingDate());
    }

    @Test
    void bookingDTOToBooking() {
        Booking booking = bookingMapper.bookingDTOToBooking(TestData.BOOKING_DTO);

        assertNotNull(booking);
        assertEquals(TestData.BOOKING_DTO.getId(), booking.getId());
        assertEquals(TestData.BOOKING_DTO.getBookingDate(), booking.getBookingDate());
        assertEquals(TestData.BOOKING_DTO.getClientId(), booking.getClient().getId());
        assertEquals(TestData.BOOKING_DTO.getTourId(), booking.getTour().getId());

    }
}