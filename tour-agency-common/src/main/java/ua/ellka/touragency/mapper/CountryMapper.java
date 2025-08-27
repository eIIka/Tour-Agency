package ua.ellka.touragency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ua.ellka.touragency.dto.CountryDTO;
import ua.ellka.touragency.model.Country;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CountryMapper {
    CountryDTO countryToCountryDTO(Country country);
    Country countryDTOToCountry(CountryDTO countryDTO);
}
