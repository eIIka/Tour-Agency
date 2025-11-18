package ua.ellka.touragency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ua.ellka.touragency.dto.CountryDTO;
import ua.ellka.touragency.model.Country;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CountryMapper {
    CountryMapper INSTANCE = Mappers.getMapper(CountryMapper.class);

    CountryDTO countryToCountryDTO(Country country);
    Country countryDTOToCountry(CountryDTO countryDTO);
}