package ua.ellka.touragency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ua.ellka.touragency.dto.CountryDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.exception.ServiceException;
import ua.ellka.touragency.mapper.CountryMapper;
import ua.ellka.touragency.model.Country;
import ua.ellka.touragency.repo.CountryRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryMapper countryMapper;
    private final CountryRepo countryRepo;

    //5
    @Override
    public CountryDTO createCountry(CountryDTO countryDTO) {
        countryRepo.findByNameAndRegion(countryDTO.getName(), countryDTO.getRegion())
                .ifPresent(exists -> {
                    throw new ExistingServiceException("Country already exists");
                });

        try {
            Country country = countryMapper.countryDTOToCountry(countryDTO);
            Country save = countryRepo.save(country);
            return countryMapper.countryToCountryDTO(save);
        }catch (DataAccessException e) {
            throw new ServiceException("Error while creating country: " + e.getMessage());
        }
    }

    //6
    @Override
    public List<CountryDTO> getAllCountries() {
        if (countryRepo.count() == 0) {
            throw new NotFoundServiceException("Countries not found");
        }

        return countryRepo.findAll().stream()
                .map(countryMapper::countryToCountryDTO)
                .toList();
    }

    //7
    @Override
    public CountryDTO deleteCountry(Long id) {
        Country existingCountry = countryRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Country not found"));

        try {
            countryRepo.delete(existingCountry);
            return countryMapper.countryToCountryDTO(existingCountry);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to delete country due to database error");
        }
    }
}
