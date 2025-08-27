package ua.ellka.touragency.service;

import ua.ellka.touragency.dto.CountryDTO;

import java.util.List;

public interface CountryService {
    CountryDTO createCountry(CountryDTO countryDTO);
    List<CountryDTO> getAllCountries();
    CountryDTO deleteCountry(Long id);

}
