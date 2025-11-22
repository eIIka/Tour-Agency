package ua.ellka.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.ellka.touragency.dto.TourDTO;
import ua.ellka.touragency.service.TourService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tour")
public class TourController {
    private final TourService tourService;

    @GetMapping
    public ResponseEntity<List<TourDTO>> getAllTours() {
        List<TourDTO> allTours = tourService.getAllTours();

        return ResponseEntity.ok(allTours);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourDTO> getTourById(@PathVariable Long id) {
        return ResponseEntity.ok(tourService.getTourById(id));
    }

    @GetMapping("/country/{countryName}")
    public ResponseEntity<List<TourDTO>> getToursByCountryName(@PathVariable(name = "countryName") String countryName) {
        List<TourDTO> getToursByCountryId = tourService.getToursByCountryName(countryName);

        return ResponseEntity.ok(getToursByCountryId);
    }

    @GetMapping("/guide/{guideName}")
    public ResponseEntity<List<TourDTO>> getToursByGuideName(@PathVariable(name = "guideName") String guideName) {
        List<TourDTO> getToursByGuideId = tourService.getToursByGuideName(guideName);

        return ResponseEntity.ok(getToursByGuideId);
    }

    @GetMapping("/guide/id/{guideId}") // <-- НОВИЙ/ВІДНОВЛЕНИЙ ЕНДПОІНТ
    public ResponseEntity<List<TourDTO>> getToursByGuideId(@PathVariable(name = "guideId") Long guideId) {
        List<TourDTO> getToursByGuideId = tourService.getToursByGuideId(guideId);

        return ResponseEntity.ok(getToursByGuideId);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<TourDTO>> getMostPopularTours() {
        List<TourDTO> popularTours = tourService.getMostPopularTours();

        return ResponseEntity.ok(popularTours);
    }

    @GetMapping("/profit/{id}")
    public ResponseEntity<BigDecimal> getTourProfit(@PathVariable(name = "id") Long id) {
        BigDecimal tourProfit = tourService.getTourProfit(id);

        return ResponseEntity.ok(tourProfit);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TourDTO> createTour(@RequestBody TourDTO tourDTO) {
        TourDTO createTour = tourService.createTour(tourDTO);

        return ResponseEntity.ok(createTour);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourDTO> updateTour(@PathVariable(name = "id") Long id,
                                           @RequestBody TourDTO tourDTO) {
        TourDTO updateTour = tourService.updateTour(id, tourDTO);

        return ResponseEntity.ok(updateTour);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TourDTO> deleteTour(@PathVariable(name = "id") Long id) {
        TourDTO deleteTour = tourService.deleteTour(id);

        return ResponseEntity.ok(deleteTour);
    }
}
