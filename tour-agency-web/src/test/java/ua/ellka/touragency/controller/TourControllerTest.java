package ua.ellka.touragency.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.ellka.touragency.dto.TourDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.exception.ServiceException;
import ua.ellka.touragency.service.TourService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class TourControllerTest extends ControllerParentTest {
    @Mock
    private TourService tourService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        TourController controller = new TourController(tourService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(globalExceptionHandler)
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllToursTest_returnsOk() throws Exception {
        when(tourService.getAllTours()).thenReturn(List.of(new TourDTO()));

        mockMvc.perform(get("/v1/tour"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllToursTest_returnsNotFound() throws Exception {
        when(tourService.getAllTours()).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/tour"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getToursByCountryIdTest_returnsOk() throws Exception {
        when(tourService.getToursByCountryId(anyLong())).thenReturn(List.of(new TourDTO()));

        mockMvc.perform(get("/v1/tour/country/{countryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getToursByCountryIdTest_returnsNotFound() throws Exception {
        when(tourService.getToursByCountryId(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/tour/country/{countryId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getToursByGuideIdTest_returnsOk() throws Exception {
        when(tourService.getToursByGuideId(anyLong())).thenReturn(List.of(new TourDTO()));

        mockMvc.perform(get("/v1/tour/guide/{guideId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getToursByGuideIdTest_returnsNotFound() throws Exception {
        when(tourService.getToursByGuideId(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/tour/guide/{guideId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getMostPopularToursTest_returnsOk() throws Exception {
        when(tourService.getMostPopularTours()).thenReturn(List.of(new TourDTO()));

        mockMvc.perform(get("/v1/tour/popular"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getMostPopularToursTest_returnsNotFound() throws Exception {
        when(tourService.getMostPopularTours()).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/tour/popular"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTourProfitTest_returnsOk() throws Exception {
        when(tourService.getTourProfit(anyLong())).thenReturn(BigDecimal.valueOf(100));

        mockMvc.perform(get("/v1/tour/profit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTourProfitTest_returnsNotFound() throws Exception {
        when(tourService.getTourProfit(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/tour/profit/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createTourTest_returnsOk() throws Exception {
        when(tourService.createTour(any())).thenReturn(new TourDTO());

        TourDTO tourDTO = new TourDTO();
        tourDTO.setName("test name tour");
        tourDTO.setPrice(new BigDecimal(200));
        tourDTO.setStartDate(LocalDate.of(2020, 1, 1));
        tourDTO.setEndDate(LocalDate.of(2020, 2, 1));
        tourDTO.setCountryId(1L);
        tourDTO.setGuideId(1L);

        String reqBody = objectMapper.writeValueAsString(tourDTO);
        mockMvc.perform(
                        post("/v1/tour")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createTourTest_returnsNotFound() throws Exception {
        when(tourService.createTour(any())).thenThrow(NotFoundServiceException.class);

        TourDTO tourDTO = new TourDTO();
        tourDTO.setName("test name tour");
        tourDTO.setPrice(new BigDecimal(200));
        tourDTO.setStartDate(LocalDate.of(2020, 1, 1));
        tourDTO.setEndDate(LocalDate.of(2020, 2, 1));
        tourDTO.setCountryId(1L);
        tourDTO.setGuideId(1L);

        String reqBody = objectMapper.writeValueAsString(tourDTO);
        mockMvc.perform(
                        post("/v1/tour")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createTourTest_returnsConflict() throws Exception {
        when(tourService.createTour(any())).thenThrow(ExistingServiceException.class);

        TourDTO tourDTO = new TourDTO();
        tourDTO.setName("test name tour");
        tourDTO.setPrice(new BigDecimal(200));
        tourDTO.setStartDate(LocalDate.of(2020, 1, 1));
        tourDTO.setEndDate(LocalDate.of(2020, 2, 1));
        tourDTO.setCountryId(1L);
        tourDTO.setGuideId(1L);

        String reqBody = objectMapper.writeValueAsString(tourDTO);
        mockMvc.perform(
                        post("/v1/tour")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateTourTest_returnsOk() throws Exception {
        when(tourService.updateTour(anyLong(), any())).thenReturn(new TourDTO());

        TourDTO tourDTO = new TourDTO();
        tourDTO.setName("test name tour");
        tourDTO.setPrice(new BigDecimal(200));
        tourDTO.setStartDate(LocalDate.of(2020, 1, 1));
        tourDTO.setEndDate(LocalDate.of(2020, 2, 1));
        tourDTO.setCountryId(1L);
        tourDTO.setGuideId(1L);

        String reqBody = objectMapper.writeValueAsString(tourDTO);
        mockMvc.perform(
                        put("/v1/tour/{id}", 1L)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateTourTest_returnsNotFound() throws Exception {
        when(tourService.updateTour(anyLong(), any())).thenThrow(NotFoundServiceException.class);

        TourDTO tourDTO = new TourDTO();
        tourDTO.setName("test name tour");
        tourDTO.setPrice(new BigDecimal(200));
        tourDTO.setStartDate(LocalDate.of(2020, 1, 1));
        tourDTO.setEndDate(LocalDate.of(2020, 2, 1));
        tourDTO.setCountryId(1L);
        tourDTO.setGuideId(1L);

        String reqBody = objectMapper.writeValueAsString(tourDTO);
        mockMvc.perform(
                        put("/v1/tour/{id}", 1L)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateTourTest_returnsConflict() throws Exception {
        when(tourService.updateTour(anyLong(), any())).thenThrow(ExistingServiceException.class);

        TourDTO tourDTO = new TourDTO();
        tourDTO.setName("test name tour");
        tourDTO.setPrice(new BigDecimal(200));
        tourDTO.setStartDate(LocalDate.of(2020, 1, 1));
        tourDTO.setEndDate(LocalDate.of(2020, 2, 1));
        tourDTO.setCountryId(1L);
        tourDTO.setGuideId(1L);

        String reqBody = objectMapper.writeValueAsString(tourDTO);
        mockMvc.perform(
                        put("/v1/tour/{id}", 1L)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteTourTest_returnsOk() throws Exception {
        when(tourService.deleteTour(anyLong())).thenReturn(new TourDTO());

        mockMvc.perform(delete("/v1/tour/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteTourTest_returnsNotFound() throws Exception {
        when(tourService.deleteTour(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(delete("/v1/tour/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteTourTest_returnsConflict() throws Exception {
        when(tourService.deleteTour(anyLong())).thenThrow(ServiceException.class);

        mockMvc.perform(delete("/v1/tour/{id}", 1L))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}