package ua.ellka.touragency.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.ellka.touragency.dto.TourDTO;
import ua.ellka.touragency.mapper.TourMapper;
import ua.ellka.touragency.model.Booking;
import ua.ellka.touragency.model.Country;
import ua.ellka.touragency.model.Guide;
import ua.ellka.touragency.model.Tour;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.repo.BookingRepo;
import ua.ellka.touragency.repo.CountryRepo;
import ua.ellka.touragency.repo.GuideRepo;
import ua.ellka.touragency.repo.TourRepo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TourServiceImplTest {
    private TourService tourService;
    private TourRepo tourRepo;
    private BookingRepo bookingRepo;
    private GuideRepo guideRepo;
    private CountryRepo countryRepo;

    private Tour tour;
    private TourDTO tourDTO;
    private Country country;
    private Guide guide;


    @BeforeEach
    void setUp() {
        TourMapper tourMapper = TourMapper.INSTANCE;
        tourRepo = mock(TourRepo.class);
        bookingRepo = mock(BookingRepo.class);
        guideRepo = mock(GuideRepo.class);
        countryRepo = mock(CountryRepo.class);
        tourService = new TourServiceImpl(tourMapper, tourRepo, bookingRepo, guideRepo, countryRepo);

        tourDTO = new TourDTO();
        tourDTO.setId(1L);
        tourDTO.setName("Tour name");
        tourDTO.setPrice(new BigDecimal("100.00"));
        tourDTO.setCountryId(1L);
        tourDTO.setGuideId(1L);
        tourDTO.setStartDate(LocalDate.of(2020, 1, 1));
        tourDTO.setEndDate(LocalDate.of(2020, 2, 1));

        country = new Country();
        country.setId(1L);
        country.setName("Ukraine");
        country.setRegion("Odessa");

        guide = new Guide();
        guide.setId(1L);
        guide.setName("Vlad Kalkatin");
        guide.setLanguage("Ukraine");

        tour = new Tour();
        tour.setId(1L);
        tour.setName("Tour name");
        tour.setPrice(new BigDecimal("100.00"));
        tour.setCountry(country);
        tour.setGuide(guide);
        tour.setStartDate(LocalDate.of(2020, 1, 1));
        tour.setEndDate(LocalDate.of(2020, 2, 1));
    }

    @Test
    void createTourTest_success() {
        when(tourRepo.findByName(any())).thenReturn(Optional.empty());
        when(countryRepo.findById(anyLong())).thenReturn(Optional.of(country));
        when(guideRepo.findById(anyLong())).thenReturn(Optional.of(guide));
        when(tourRepo.save(any())).thenReturn(tour);

        TourDTO created = tourService.createTour(tourDTO);

        assertNotNull(created);
        assertEquals(tourDTO.getId(), created.getId());
        assertEquals(tourDTO.getName(), created.getName());
        assertEquals(tourDTO.getPrice(), created.getPrice());
        assertEquals(tourDTO.getCountryId(), created.getCountryId());
        assertEquals(tourDTO.getGuideId(), created.getGuideId());
        assertEquals(tourDTO.getStartDate(), created.getStartDate());
        assertEquals(tourDTO.getEndDate(), created.getEndDate());
    }

    @Test
    void createTourTest_tourNameAlreadyExistsException() {
        when(tourRepo.findByName(any())).thenReturn(Optional.of(tour));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> tourService.createTour(tourDTO));
        assertEquals("Name already exists", exception.getMessage());
    }

    @Test
    void createTourTest_countryNotFoundException() {
        when(tourRepo.findByName(any())).thenReturn(Optional.empty());
        when(countryRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.createTour(tourDTO));
        assertEquals("Country not found", exception.getMessage());
    }

    @Test
    void createTourTest_guideNotFoundException() {
        when(tourRepo.findByName(any())).thenReturn(Optional.empty());
        when(countryRepo.findById(anyLong())).thenReturn(Optional.of(country));
        when(guideRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.createTour(tourDTO));
        assertEquals("Guide not found", exception.getMessage());
    }

    @Test
    void getAllToursTest_success() {
        when(tourRepo.count()).thenReturn(1L);
        when(tourRepo.findAll()).thenReturn(List.of(tour));

        List<TourDTO> allTours = tourService.getAllTours();

        assertNotNull(allTours);
        assertEquals(1, allTours.size());
        TourDTO tourDTOList = allTours.get(0);
        assertEquals(tourDTO.getId(), tourDTOList.getId());
        assertEquals(tourDTO.getName(), tourDTOList.getName());
        assertEquals(tourDTO.getPrice(), tourDTOList.getPrice());
        assertEquals(tourDTO.getCountryId(), tourDTOList.getCountryId());
        assertEquals(tourDTO.getGuideId(), tourDTOList.getGuideId());
        assertEquals(tourDTO.getStartDate(), tourDTOList.getStartDate());
        assertEquals(tourDTO.getEndDate(), tourDTOList.getEndDate());
    }

    @Test
    void getAllToursTest_toursNotFoundException() {
        when(tourRepo.count()).thenReturn(0L);

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.getAllTours());
        assertEquals("Tours not found", exception.getMessage());
    }

    @Test
    void updateTourTest_success() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));
        when(tourRepo.findByName(any())).thenReturn(Optional.empty());
        when(countryRepo.findById(anyLong())).thenReturn(Optional.of(country));
        when(guideRepo.findById(anyLong())).thenReturn(Optional.of(guide));
        when(tourRepo.save(any())).thenReturn(tour);

        TourDTO updated = tourService.updateTour(1L, tourDTO);

        assertNotNull(updated);
        assertEquals(tourDTO.getId(), updated.getId());
        assertEquals(tourDTO.getName(), updated.getName());
        assertEquals(tourDTO.getPrice(), updated.getPrice());
        assertEquals(tourDTO.getCountryId(), updated.getCountryId());
        assertEquals(tourDTO.getGuideId(), updated.getGuideId());
        assertEquals(tourDTO.getStartDate(), updated.getStartDate());
        assertEquals(tourDTO.getEndDate(), updated.getEndDate());
    }

    @Test
    void updateTourTest_tourNotFoundException() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.updateTour(1L, tourDTO));
        assertEquals("Tour not found", exception.getMessage());
    }

    @Test
    void updateTourTest_tourNameAlreadyExistsException() {
        Tour tour2 = new Tour();
        tour2.setId(2L);
        tour2.setName("Tour name");

        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));
        when(tourRepo.findByName(any())).thenReturn(Optional.of(tour2));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> tourService.updateTour(1L, tourDTO));
        assertEquals("Name already exists", exception.getMessage());
    }

    @Test
    void updateTourTest_countryNotFoundException() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));
        when(tourRepo.findByName(any())).thenReturn(Optional.empty());
        when(countryRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.updateTour(1L, tourDTO));
        assertEquals("Country not found", exception.getMessage());
    }

    @Test
    void updateTourTest_guideNotFoundException() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));
        when(tourRepo.findByName(any())).thenReturn(Optional.empty());
        when(countryRepo.findById(anyLong())).thenReturn(Optional.of(country));
        when(guideRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.updateTour(1L, tourDTO));
        assertEquals("Guide not found", exception.getMessage());
    }

    @Test
    void deleteTourTest_success() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));

        TourDTO deleted = tourService.deleteTour(1L);

        assertNotNull(deleted);
        assertEquals(tourDTO.getId(), deleted.getId());
        assertEquals(tourDTO.getName(), deleted.getName());
        assertEquals(tourDTO.getPrice(), deleted.getPrice());
        assertEquals(tourDTO.getCountryId(), deleted.getCountryId());
        assertEquals(tourDTO.getGuideId(), deleted.getGuideId());
        assertEquals(tourDTO.getStartDate(), deleted.getStartDate());
        assertEquals(tourDTO.getEndDate(), deleted.getEndDate());
    }

    @Test
    void deleteTourTest_tourNotFoundException() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.deleteTour(1L));
        assertEquals("Tour not found", exception.getMessage());
    }

    @Test
    void getToursByCountryIdTest_success() {
        when(countryRepo.findById(anyLong())).thenReturn(Optional.of(country));
        when(tourRepo.findToursByCountryId(anyLong())).thenReturn(List.of(tour));

        List<TourDTO> tours = tourService.getToursByCountryId(1L);

        assertNotNull(tours);
        assertEquals(1, tours.size());
        TourDTO tourDTOList = tours.get(0);
        assertEquals(tourDTO.getId(), tourDTOList.getId());
        assertEquals(tourDTO.getName(), tourDTOList.getName());
        assertEquals(tourDTO.getPrice(), tourDTOList.getPrice());
        assertEquals(tourDTO.getCountryId(), tourDTOList.getCountryId());
        assertEquals(tourDTO.getGuideId(), tourDTOList.getGuideId());
        assertEquals(tourDTO.getStartDate(), tourDTOList.getStartDate());
        assertEquals(tourDTO.getEndDate(), tourDTOList.getEndDate());
    }

    @Test
    void getToursByCountryIdTest_countryNotFoundException() {
        when(countryRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.getToursByCountryId(1L));
        assertEquals("Country not found", exception.getMessage());
    }

    @Test
    void getToursByCountryIdTest_toursByCountryIdNotFoundException() {
        when(countryRepo.findById(anyLong())).thenReturn(Optional.of(country));
        when(tourRepo.findToursByCountryId(anyLong())).thenReturn(List.of());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.getToursByCountryId(1L));
        assertEquals("No Tours found for country id 1", exception.getMessage());
    }

    @Test
    void getToursByGuideIdTest_success() {
        when(guideRepo.findById(anyLong())).thenReturn(Optional.of(guide));
        when(tourRepo.findToursByGuideId(anyLong())).thenReturn(List.of(tour));

        List<TourDTO> tours = tourService.getToursByGuideId(1L);

        assertNotNull(tours);
        assertEquals(1, tours.size());
        TourDTO tourDTOList = tours.get(0);
        assertEquals(tourDTO.getId(), tourDTOList.getId());
        assertEquals(tourDTO.getName(), tourDTOList.getName());
        assertEquals(tourDTO.getPrice(), tourDTOList.getPrice());
        assertEquals(tourDTO.getCountryId(), tourDTOList.getCountryId());
        assertEquals(tourDTO.getGuideId(), tourDTOList.getGuideId());
        assertEquals(tourDTO.getStartDate(), tourDTOList.getStartDate());
        assertEquals(tourDTO.getEndDate(), tourDTOList.getEndDate());
    }

    @Test
    void getToursByGuideIdTest_guideNotFoundException() {
        when(guideRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.getToursByGuideId(1L));
        assertEquals("Guide not found", exception.getMessage());
    }

    @Test
    void getToursByGuideIdTest_toursByGuideIdNotFoundException() {
        when(guideRepo.findById(anyLong())).thenReturn(Optional.of(guide));
        when(tourRepo.findToursByGuideId(anyLong())).thenReturn(List.of());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.getToursByGuideId(1L));
        assertEquals("No Tours found for guide id 1", exception.getMessage());
    }

    @Test
    void getMostPopularToursTest_success() {
        Tour tour2 = new Tour();
        tour2.setId(2L);
        tour2.setName("Tour 2");

        TourDTO tourDTO2 = new TourDTO();
        tourDTO2.setId(2L);
        tourDTO2.setName("Tour 2");

        BookingRepo.TourBookingCountResult result1 = mock(BookingRepo.TourBookingCountResult.class);
        BookingRepo.TourBookingCountResult result2 = mock(BookingRepo.TourBookingCountResult.class);

        when(result1.getTourId()).thenReturn(1L);
        when(result1.getBookingCount()).thenReturn(2L);
        when(result2.getTourId()).thenReturn(2L);
        when(result2.getBookingCount()).thenReturn(1L);

        when(tourRepo.count()).thenReturn(2L);
        when(bookingRepo.count()).thenReturn(2L);
        when(bookingRepo.findBookingCountsByTour()).thenReturn(List.of(result1, result2));
        when(tourRepo.findById(1L)).thenReturn(Optional.of(tour));
        when(tourRepo.findById(2L)).thenReturn(Optional.of(tour2));

        List<TourDTO> popularTours = tourService.getMostPopularTours();

        assertNotNull(popularTours);
        assertEquals(2, popularTours.size());

        TourDTO tourDTO1List = popularTours.get(0);
        assertEquals(tourDTO1List.getId(), tourDTO.getId());
        assertEquals(tourDTO1List.getName(), tourDTO.getName());
        assertEquals(tourDTO1List.getPrice(), tourDTO.getPrice());
        assertEquals(tourDTO1List.getCountryId(), tourDTO.getCountryId());
        assertEquals(tourDTO1List.getGuideId(), tourDTO.getGuideId());
        assertEquals(tourDTO1List.getStartDate(), tourDTO.getStartDate());
        assertEquals(tourDTO1List.getEndDate(), tourDTO.getEndDate());

        TourDTO tourDTO2List = popularTours.get(1);
        assertEquals(tourDTO2List.getId(), tourDTO2.getId());
        assertEquals(tourDTO2List.getName(), tourDTO2.getName());
    }

    @Test
    void getMostPopularTours_toursNotFoundException() {
        when(tourRepo.count()).thenReturn(0L);

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.getMostPopularTours());
        assertEquals("Tours not found", exception.getMessage());
    }

    @Test
    void getMostPopularToursTest_bookingsNotFoundException() {
        when(tourRepo.count()).thenReturn(1L);
        when(bookingRepo.count()).thenReturn(0L);

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.getMostPopularTours());
        assertEquals("No Bookings found in all tours", exception.getMessage());
    }

    @Test
    void getTourProfitTest_success() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTour(tour);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setTour(tour);

        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));
        when(bookingRepo.findBookingsByTourId(anyLong())).thenReturn(List.of(booking, booking2));

        BigDecimal tourProfit = tourService.getTourProfit(1L);

        assertNotNull(tourProfit);
        assertEquals(new BigDecimal("200.00"), tourProfit);
    }

    @Test
    void getTourProfitTest_tourFoundException() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.getTourProfit(1L));
        assertEquals("Tour not found", exception.getMessage());
    }

    @Test
    void getTourProfitTest_bookingsNotFoundException() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(tour));
        when(bookingRepo.findBookingsByTourId(anyLong())).thenReturn(List.of());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> tourService.getTourProfit(1L));
        assertEquals("Profit for the tour cannot be determined due to the lack of bookings", exception.getMessage());
    }
}