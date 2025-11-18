package ua.ellka.touragency.mapper;

import org.junit.jupiter.api.Test;
import ua.ellka.touragency.dto.BookingDTO;
import ua.ellka.touragency.model.Booking;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void bookingToBookingDTO() {
        BookingDTO bookingDTO = BookingMapper.INSTANCE.bookingToBookingDTO(TestData.BOOKING);

        assertNotNull(bookingDTO);
        assertEquals(TestData.BOOKING.getId(), bookingDTO.getId());
        assertEquals(TestData.BOOKING.getTour().getId(), bookingDTO.getTourId());
        assertEquals(TestData.BOOKING.getClient().getId(), bookingDTO.getClientId());
        assertEquals(TestData.BOOKING.getBookingDate(), bookingDTO.getBookingDate());
    }

    @Test
    void bookingDTOToBooking() {
        Booking booking = BookingMapper.INSTANCE.bookingDTOToBooking(TestData.BOOKING_DTO);

        assertNotNull(booking);
        assertEquals(TestData.BOOKING_DTO.getId(), booking.getId());
        assertEquals(TestData.BOOKING_DTO.getBookingDate(), booking.getBookingDate());
        assertEquals(TestData.BOOKING_DTO.getClientId(), booking.getClient().getId());
        assertEquals(TestData.BOOKING_DTO.getTourId(), booking.getTour().getId());

    }
}