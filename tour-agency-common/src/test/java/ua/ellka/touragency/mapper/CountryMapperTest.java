package ua.ellka.touragency.mapper;

import org.junit.jupiter.api.Test;
import ua.ellka.touragency.dto.CountryDTO;
import ua.ellka.touragency.model.Country;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CountryMapperTest {

    @Test
    void countryToCountryDTO() {
        CountryDTO countryDTO = CountryMapper.INSTANCE.countryToCountryDTO(TestData.COUNTRY);

        assertNotNull(countryDTO);
        assertEquals(TestData.COUNTRY.getId(), countryDTO.getId());
        assertEquals(TestData.COUNTRY.getName(), countryDTO.getName());
        assertEquals(TestData.COUNTRY.getRegion(), countryDTO.getRegion());
    }

    @Test
    void countryDTOToCountry() {
        Country country = CountryMapper.INSTANCE.countryDTOToCountry(TestData.COUNTRY_DTO);

        assertNotNull(country);
        assertEquals(TestData.COUNTRY_DTO.getId(), country.getId());
        assertEquals(TestData.COUNTRY_DTO.getName(), country.getName());
        assertEquals(TestData.COUNTRY_DTO.getRegion(), country.getRegion());
    }
}
