package ua.ellka.touragency.service;

import ua.ellka.touragency.dto.TourDTO;

import java.math.BigDecimal;
import java.util.List;

public interface TourService {
    TourDTO createTour(TourDTO tourDTO);
    List<TourDTO> getAllTours();
    TourDTO updateTour(Long id, TourDTO tourDTO);
    TourDTO deleteTour(Long id);
    List<TourDTO> getToursByGuideName(String guideName);
    List<TourDTO> getToursByGuideId(Long guideId);
    List<TourDTO> getToursByCountryName(String countryName);
    List<TourDTO> getMostPopularTours();
    BigDecimal getTourProfit(Long id);
    TourDTO getTourById(Long id);
}
