package ua.ellka.touragency.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.ellka.touragency.dto.CountryDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.exception.ServiceException;
import ua.ellka.touragency.service.CountryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CountryControllerTest extends ControllerParentTest {

    @Mock
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        CountryController countryController = new CountryController(countryService);

        mockMvc = MockMvcBuilders.standaloneSetup(countryController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void getAllCountriesTest_returnsOk() throws Exception {
        when(countryService.getAllCountries()).thenReturn(List.of(new CountryDTO()));

        mockMvc.perform(get("/v1/country"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllCountriesTest_returnsNotFound() throws Exception {
        when(countryService.getAllCountries()).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/country"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createCountryTest_returnsOk() throws Exception {
        when(countryService.createCountry(any())).thenReturn(new CountryDTO());

        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setName("Test country");
        countryDTO.setRegion("Test region");

        String reqBody = objectMapper.writeValueAsString(countryDTO);
        mockMvc.perform(
                        post("/v1/country")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createCountryTest_returnsConflict() throws Exception {
        when(countryService.createCountry(any())).thenThrow(ExistingServiceException.class);

        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setName("Test country");
        countryDTO.setRegion("Test region");

        String reqBody = objectMapper.writeValueAsString(countryDTO);
        mockMvc.perform(
                        post("/v1/country")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteCountryTest_returnsOk() throws Exception {
        when(countryService.deleteCountry(anyLong())).thenReturn(new CountryDTO());

        mockMvc.perform(delete("/v1/country/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteCountryTest_returnsNotFound() throws Exception {
        when(countryService.deleteCountry(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(delete("/v1/country/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteCountryTest_returnsConflict() throws Exception {
        when(countryService.deleteCountry(anyLong())).thenThrow(ServiceException.class);

        mockMvc.perform(delete("/v1/country/{id}", 1L))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}