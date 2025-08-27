package ua.ellka.touragency.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.ellka.touragency.dto.BookingDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.exception.ServiceException;
import ua.ellka.touragency.service.BookingService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class BookingControllerTest extends ControllerParentTest {
    @Mock
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        BookingController bookingController = new BookingController(bookingService);

        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllBookingsByClientIdTest_returnsOk() throws Exception {
        when(bookingService.getAllBookingsByClientId(anyLong())).thenReturn(List.of(new BookingDTO()));

        mockMvc.perform(get("/v1/booking/client/{clientId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllBookingsByClientIdTest_returnsNotFound() throws Exception {
        when(bookingService.getAllBookingsByClientId(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/booking/client/{clientId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getBookingsForTourIdTest_returnsOk() throws Exception {
        when(bookingService.getBookingsForTourId(anyLong())).thenReturn(List.of(new BookingDTO()));

        mockMvc.perform(get("/v1/booking/tour/{tourId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getBookingsForTourIdTest_returnsNotFound() throws Exception {
        when(bookingService.getBookingsForTourId(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/booking/tour/{tourId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getBookingStatisticsByMonthTest_returnsOk() throws Exception {
        when(bookingService.getBookingStatisticsByMonth()).thenReturn(new HashMap<>());

        mockMvc.perform(get("/v1/booking/statisticsByMonth"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getBookingStatisticsByMonthTest_returnsNotFound() throws Exception {
        when(bookingService.getBookingStatisticsByMonth()).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/booking/statisticsByMonth"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createBookingTest_returnsOk() throws Exception {
        when(bookingService.createBooking(any())).thenReturn(new BookingDTO());

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setClientId(1L);
        bookingDTO.setTourId(1L);
        bookingDTO.setBookingDate(LocalDate.of(2020, 1, 1));

        String reqBody = objectMapper.writeValueAsString(bookingDTO);
        mockMvc.perform(
                        post("/v1/booking")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createBookingTest_returnsNotFound() throws Exception {
        when(bookingService.createBooking(any())).thenThrow(NotFoundServiceException.class);

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setClientId(1L);
        bookingDTO.setTourId(1L);
        bookingDTO.setBookingDate(LocalDate.of(2020, 1, 1));

        String reqBody = objectMapper.writeValueAsString(bookingDTO);
        mockMvc.perform(
                        post("/v1/booking")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createBookingTest_returnsConflict() throws Exception {
        when(bookingService.createBooking(any())).thenThrow(ExistingServiceException.class);

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setClientId(1L);
        bookingDTO.setTourId(1L);
        bookingDTO.setBookingDate(LocalDate.of(2020, 1, 1));

        String reqBody = objectMapper.writeValueAsString(bookingDTO);
        mockMvc.perform(
                        post("/v1/booking")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteBookingTest_returnsOk() throws Exception {
        when(bookingService.deleteBooking(anyLong())).thenReturn(new BookingDTO());

        mockMvc.perform(delete("/v1/booking/{bookingId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteBookingTest_returnsNotFound() throws Exception {
        when(bookingService.deleteBooking(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(delete("/v1/booking/{bookingId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteBookingTest_returnsConflict() throws Exception {
        when(bookingService.deleteBooking(anyLong())).thenThrow(ServiceException.class);

        mockMvc.perform(delete("/v1/booking/{bookingId}", 1L))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}