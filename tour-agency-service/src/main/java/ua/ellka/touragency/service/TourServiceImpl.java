package ua.ellka.touragency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ua.ellka.touragency.dto.TourDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.exception.ServiceException;
import ua.ellka.touragency.mapper.TourMapper;
import ua.ellka.touragency.model.Booking;
import ua.ellka.touragency.model.Country;
import ua.ellka.touragency.model.Guide;
import ua.ellka.touragency.model.Tour;
import ua.ellka.touragency.repo.BookingRepo;
import ua.ellka.touragency.repo.CountryRepo;
import ua.ellka.touragency.repo.GuideRepo;
import ua.ellka.touragency.repo.TourRepo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final TourMapper tourMapper;
    private final TourRepo tourRepo;
    private final BookingRepo bookingRepo;
    private final GuideRepo guideRepo;
    private final CountryRepo countryRepo;

    //1
    @Override
    public TourDTO createTour(TourDTO tourDTO) {
        tourRepo.findByName(tourDTO.getName())
                .ifPresent(exists -> {
                    throw new ExistingServiceException("Name already exists");
                });

        countryRepo.findById(tourDTO.getCountryId())
                .orElseThrow(() -> new NotFoundServiceException("Country not found"));

        guideRepo.findById(tourDTO.getGuideId())
                .orElseThrow(() -> new NotFoundServiceException("Guide not found"));

        try {
            Tour tour = tourMapper.tourDTOToTour(tourDTO);
            Tour save = tourRepo.save(tour);
            return tourMapper.tourToTourDTO(save);
        }catch (DataAccessException e) {
            throw new ServiceException("Error while creating tour: " + e.getMessage());
        }
    }

    //2
    @Override
    public List<TourDTO> getAllTours() {
        if (tourRepo.count() == 0) {
            throw new NotFoundServiceException("Tours not found");
        }

        return tourRepo.findAll().stream()
                .map(tourMapper::tourToTourDTO)
                .toList();
    }

    //3
    @Override
    @PreAuthorize("@accessChecker.isTourOwner(#id) || hasRole('ROLE_ADMIN')")
    public TourDTO updateTour(Long id, TourDTO tourDTO) {
        Tour updateTour = tourRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Tour not found"));

        tourRepo.findByName(tourDTO.getName())
                .filter(tour -> !tour.getId().equals(id))
                .ifPresent(exists -> {
                    throw new ExistingServiceException("Name already exists");
                });

        Country country = countryRepo.findById(tourDTO.getCountryId())
                .orElseThrow(() -> new NotFoundServiceException("Country not found"));

        Guide guide = guideRepo.findById(tourDTO.getGuideId())
                .orElseThrow(() -> new NotFoundServiceException("Guide not found"));

        Tour tour = tourMapper.tourDTOToTour(tourDTO);

        updateTour.setId(id);
        updateTour.setName(tour.getName());
        updateTour.setCountry(country);
        updateTour.setPrice(tour.getPrice());
        updateTour.setGuide(guide);
        updateTour.setEndDate(tour.getEndDate());
        updateTour.setStartDate(tour.getStartDate());

        try {
            Tour save = tourRepo.save(updateTour);
            return tourMapper.tourToTourDTO(save);
        }catch (DataAccessException e) {
            throw new ServiceException("Error while updating tour: " + e.getMessage());
        }
    }

    //4
    @Override
    @PreAuthorize("@accessChecker.isTourOwner(#id) || hasRole('ROLE_ADMIN')")
    public TourDTO deleteTour(Long id) {
        Tour existingTour = tourRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Tour not found"));

        try {
            tourRepo.delete(existingTour);
            return tourMapper.tourToTourDTO(existingTour);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to delete tour due to database error");
        }
    }

    //20
    @Override
    public List<TourDTO> getToursByCountryId(Long countryId) {
        countryRepo.findById(countryId)
                .orElseThrow(() -> new NotFoundServiceException("Country not found"));

        List<Tour> toursByCountryId = tourRepo.findToursByCountryId(countryId);
        if (toursByCountryId.isEmpty()) {
            throw new NotFoundServiceException("No Tours found for country id " + countryId);
        }

        return toursByCountryId.stream()
                .map(tourMapper::tourToTourDTO)
                .toList();
    }

    //21
    @Override
    public List<TourDTO> getToursByGuideId(Long guideId) {
        guideRepo.findById(guideId)
                .orElseThrow(() -> new NotFoundServiceException("Guide not found"));

        List<Tour> toursByGuideId = tourRepo.findToursByGuideId(guideId);
        if (toursByGuideId.isEmpty()) {
            throw new NotFoundServiceException("No Tours found for guide id " + guideId);
        }

        return toursByGuideId.stream()
                .map(tourMapper::tourToTourDTO)
                .toList();
    }

    //24
    @Override
    public List<TourDTO> getMostPopularTours() {
        if (tourRepo.count() == 0) {
            throw new NotFoundServiceException("Tours not found");
        }

        if (bookingRepo.count() == 0) {
            throw new NotFoundServiceException("No Bookings found in all tours");
        }

        List<BookingRepo.TourBookingCountResult> results = bookingRepo.findBookingCountsByTour();

        List<Tour> popularTours = results.stream()
                .sorted((result1, result2) ->
                        Long.compare(result2.getBookingCount(), result1.getBookingCount()))
                .map(result ->
                        tourRepo.findById(result.getTourId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return popularTours.stream()
                .map(tourMapper::tourToTourDTO)
                .toList();
    }

    //25
    @Override
    //@PreAuthorize("@accessChecker.isTourOwner(#id) || hasRole('ADMIN')")
    public BigDecimal getTourProfit(Long id) {
        Tour tour = tourRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Tour not found"));

        List<Booking> bookingsForTour = bookingRepo.findBookingsByTourId(id);
        if (bookingsForTour.isEmpty()) {
            throw new NotFoundServiceException("Profit for the tour cannot be determined due to the lack of bookings");
        }

        BigDecimal tourPrice = tour.getPrice();
        int bookingCount = bookingsForTour.size();
        return tourPrice.multiply(BigDecimal.valueOf(bookingCount));
    }
}
