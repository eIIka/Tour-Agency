package ua.ellka.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.ellka.touragency.dto.CountryDTO;
import ua.ellka.touragency.service.CountryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/country")
public class CountryController {
    private final CountryService countryService;

    @GetMapping
    public ResponseEntity<List<CountryDTO>> getAllCountries() {
        List<CountryDTO> allCountries = countryService.getAllCountries();

        return ResponseEntity.ok(allCountries);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CountryDTO> createCountry(@RequestBody CountryDTO countryDTO) {
        CountryDTO createdCountry = countryService.createCountry(countryDTO);

        return ResponseEntity.ok(createdCountry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CountryDTO> deleteCountry(@PathVariable(name = "id") Long id) {
        CountryDTO deleteCountry = countryService.deleteCountry(id);

        return ResponseEntity.ok(deleteCountry);
    }
}
