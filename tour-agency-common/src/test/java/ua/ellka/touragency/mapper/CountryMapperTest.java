package ua.ellka.touragency.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ua.ellka.touragency.dto.CountryDTO;
import ua.ellka.touragency.model.Country;

import static org.junit.jupiter.api.Assertions.*;

class CountryMapperTest {
    private CountryMapper countryMapper;

    @BeforeEach
    void setUp() {
        countryMapper = Mappers.getMapper(CountryMapper.class);
    }

    @Test
    void countryToCountryDTO() {
        CountryDTO countryDTO = countryMapper.countryToCountryDTO(TestData.COUNTRY);

        assertNotNull(countryDTO);
        assertEquals(TestData.COUNTRY.getId(), countryDTO.getId());
        assertEquals(TestData.COUNTRY.getName(), countryDTO.getName());
        assertEquals(TestData.COUNTRY.getRegion(), countryDTO.getRegion());
    }

    @Test
    void countryDTOToCountry() {
        Country country = countryMapper.countryDTOToCountry(TestData.COUNTRY_DTO);

        assertNotNull(country);
        assertEquals(TestData.COUNTRY_DTO.getId(), country.getId());
        assertEquals(TestData.COUNTRY_DTO.getName(), country.getName());
        assertEquals(TestData.COUNTRY_DTO.getRegion(), country.getRegion());
    }
}