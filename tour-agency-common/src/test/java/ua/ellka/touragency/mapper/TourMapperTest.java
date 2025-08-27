package ua.ellka.touragency.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ua.ellka.touragency.dto.TourDTO;
import ua.ellka.touragency.model.Tour;

import static org.junit.jupiter.api.Assertions.*;

class TourMapperTest {
    private TourMapper tourMapper;
    @BeforeEach
    void setUp() {
        tourMapper = Mappers.getMapper(TourMapper.class);
    }

    @Test
    void tourToTourDTO() {
        TourDTO tourDTO = tourMapper.tourToTourDTO(TestData.TOUR);

        assertNotNull(tourDTO);
        assertEquals(TestData.TOUR.getId(), tourDTO.getId());
        assertEquals(TestData.TOUR.getName(), tourDTO.getName());
        assertEquals(TestData.TOUR.getPrice(), tourDTO.getPrice());
        assertEquals(TestData.TOUR.getStartDate(), tourDTO.getStartDate());
        assertEquals(TestData.TOUR.getEndDate(), tourDTO.getEndDate());
        assertEquals(TestData.TOUR.getCountry().getId(), tourDTO.getCountryId());
        assertEquals(TestData.TOUR.getGuide().getId(), tourDTO.getGuideId());
    }

    @Test
    void tourDTOToTour() {
        Tour tour = tourMapper.tourDTOToTour(TestData.TOUR_DTO);

        assertNotNull(tour);
        assertEquals(TestData.TOUR_DTO.getId(), tour.getId());
        assertEquals(TestData.TOUR_DTO.getName(), tour.getName());
        assertEquals(TestData.TOUR_DTO.getPrice(), tour.getPrice());
        assertEquals(TestData.TOUR_DTO.getStartDate(), tour.getStartDate());
        assertEquals(TestData.TOUR_DTO.getEndDate(), tour.getEndDate());
        assertEquals(TestData.TOUR_DTO.getCountryId(), tour.getCountry().getId());
        assertEquals(TestData.TOUR_DTO.getGuideId(), tour.getGuide().getId());
    }
}