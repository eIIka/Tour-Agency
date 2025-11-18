package ua.ellka.touragency.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.ellka.touragency.dto.CountryDTO;
import ua.ellka.touragency.mapper.CountryMapper;
import ua.ellka.touragency.model.Country;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.repo.CountryRepo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CountryServiceImplTest {

    private CountryService countryService;
    private CountryRepo countryRepo;
    private Country country;
    private CountryDTO countryDTO;

    @BeforeEach
    void setUp() {
        CountryMapper countryMapper = CountryMapper.INSTANCE;
        countryRepo = mock(CountryRepo.class);
        countryService = new CountryServiceImpl(countryMapper, countryRepo);

        country = new Country();
        country.setId(1L);
        country.setName("Ukraine");
        country.setRegion("Odessa");

        countryDTO = new CountryDTO();
        countryDTO.setId(1L);
        countryDTO.setName("Ukraine");
        countryDTO.setRegion("Odessa");
    }

    @Test
    void createCountryTest_success() {
        when(countryRepo.findByNameAndRegion(any(), any())).thenReturn(Optional.empty());
        when(countryRepo.save(any())).thenReturn(country);

        CountryDTO created = countryService.createCountry(countryDTO);

        assertNotNull(created);
        assertEquals(countryDTO.getId(), created.getId());
        assertEquals(countryDTO.getName(), created.getName());
        assertEquals(countryDTO.getRegion(), created.getRegion());
    }

    @Test
    void createCountryTest_countryExistingException() {
        when(countryRepo.findByNameAndRegion(any(), any())).thenReturn(Optional.of(country));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> countryService.createCountry(countryDTO));
        assertEquals("Country already exists", exception.getMessage());
    }

    @Test
    void getAllCountriesTest_success() {
        when(countryRepo.count()).thenReturn(1L);
        when(countryRepo.findAll()).thenReturn(List.of(country));

        List<CountryDTO> countries = countryService.getAllCountries();

        assertNotNull(countries);
        assertEquals(1, countries.size());
        assertEquals(countryDTO.getId(), countries.get(0).getId());
        assertEquals(countryDTO.getName(), countries.get(0).getName());
        assertEquals(countryDTO.getRegion(), countries.get(0).getRegion());
    }

    @Test
    void getAllCountriesTest_countryNotFoundServiceException() {
        when(countryRepo.count()).thenReturn(0L);

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> countryService.getAllCountries());
        assertEquals("Countries not found", exception.getMessage());
    }

    @Test
    void deleteCountryTest_success() {
        when(countryRepo.findById(anyLong())).thenReturn(Optional.of(country));

        CountryDTO deleted = countryService.deleteCountry(1L);

        assertNotNull(deleted);
        assertEquals(countryDTO.getId(), deleted.getId());
        assertEquals(countryDTO.getName(), deleted.getName());
        assertEquals(countryDTO.getRegion(), deleted.getRegion());
    }

    @Test
    void deleteCountryTest_countryNotFoundServiceException() {
        when(countryRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> countryService.deleteCountry(1L));
        assertEquals("Country not found", exception.getMessage());
    }

}