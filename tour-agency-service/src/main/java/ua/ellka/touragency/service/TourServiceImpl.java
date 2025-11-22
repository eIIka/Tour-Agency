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
import java.util.Optional;

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
        // 1. Перевірка унікальності імені туру
        tourRepo.findByName(tourDTO.getName())
                .ifPresent(exists -> {
                    throw new ExistingServiceException("Tour name already exists");
                });

        // 2. Логіка Upsert для Країни
        Country country;
        if (tourDTO.getCountryName() != null && !tourDTO.getCountryName().isBlank()) {

            Optional<Country> existingCountry = countryRepo.findByNameAndRegion(tourDTO.getCountryName(), tourDTO.getCountryRegion());

            if (existingCountry.isEmpty()) {
                // Країна не знайдена -> Створюємо нову країну
                country = new Country();
                country.setName(tourDTO.getCountryName());
                country.setRegion(tourDTO.getCountryRegion() != null ? tourDTO.getCountryRegion() : "Unknown Region");
                country = countryRepo.save(country);
            } else {
                country = existingCountry.get();
            }
        } else {
            // Якщо назва не передана
            throw new NotFoundServiceException("Country name is required.");
        }

        // 3. Перевірка Гіда (ID має прийти з фронтенду)
        Guide guide = guideRepo.findById(tourDTO.getGuideId())
                .orElseThrow(() -> new NotFoundServiceException("Guide not found with ID: " + tourDTO.getGuideId()));

        try {
            // 4. Мапінг простих полів та ручне присвоєння об'єктів
            Tour tour = tourMapper.tourDTOToTour(tourDTO);

            // ВАЖЛИВО: Присвоюємо об'єкти, які ми знайшли/створили в БД
            tour.setCountry(country);
            tour.setGuide(guide);
            tour.setImageUrl(tourDTO.getImageUrl());

            Tour save = tourRepo.save(tour);

            // Повертаємо DTO з повною інформацією
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
        // 1. Знаходимо існуючий тур для оновлення
        Tour updateTour = tourRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Tour not found with ID: " + id));

        // 2. Перевірка унікальності імені туру (якщо ім'я змінилося)
        tourRepo.findByName(tourDTO.getName())
                .filter(tour -> !tour.getId().equals(id)) // Виключаємо поточний тур із перевірки
                .ifPresent(exists -> {
                    throw new ExistingServiceException("Tour name already exists");
                });

        // 3. Логіка Upsert для Країни
        Country country;

        // Перевіряємо, чи була передана назва країни (з фронтенду)
        if (tourDTO.getCountryName() != null && !tourDTO.getCountryName().isBlank()) {

            // Спробуємо знайти країну за назвою та регіоном
            Optional<Country> existingCountry = countryRepo.findByNameAndRegion(tourDTO.getCountryName(), tourDTO.getCountryRegion());

            if (existingCountry.isEmpty()) {
                // Країна не знайдена -> Створюємо нову країну
                country = new Country();
                country.setName(tourDTO.getCountryName());
                country.setRegion(tourDTO.getCountryRegion() != null ? tourDTO.getCountryRegion() : "Unknown Region");
                country = countryRepo.save(country);
            } else {
                // Країна знайдена -> Використовуємо існуючий об'єкт
                country = existingCountry.get();
            }
        } else {
            // Якщо назва країни не передана, залишаємо ту, що була
            country = updateTour.getCountry();
        }

        // 4. Перевірка Гіда (ID має бути в DTO, якщо це зміна гіда)
        Guide guide;
        if (tourDTO.getGuideId() != null) {
            guide = guideRepo.findById(tourDTO.getGuideId())
                    .orElseThrow(() -> new NotFoundServiceException("Guide not found with ID: " + tourDTO.getGuideId()));
        } else {
            // Залишаємо поточного гіда
            guide = updateTour.getGuide();
        }

        try {
            // 5. Мапінг простих полів DTO до моделі Tour
            Tour mappedTour = tourMapper.tourDTOToTour(tourDTO);

            // 6. Присвоєння оновлених/знайдених об'єктів
            updateTour.setName(mappedTour.getName());
            updateTour.setPrice(mappedTour.getPrice());
            updateTour.setStartDate(mappedTour.getStartDate());
            updateTour.setEndDate(mappedTour.getEndDate());

            // Оновлюємо зв'язки
            updateTour.setCountry(country);
            updateTour.setGuide(guide);

            updateTour.setImageUrl(tourDTO.getImageUrl());

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
    public List<TourDTO> getToursByCountryName(String countryName) {
        List<Country> byName = countryRepo.findByName(countryName);
        if (byName.isEmpty()) {
            throw new NotFoundServiceException("Country not found with name: " + countryName);
        }

        String name = byName.get(0).getName();

        List<Tour> toursByCountryName = tourRepo.findToursByCountryName(name);
        if (toursByCountryName.isEmpty()) {
            throw new NotFoundServiceException("No Tours found for country name " + name);
        }

        return toursByCountryName.stream()
                .map(tourMapper::tourToTourDTO)
                .toList();
    }

    //21
    @Override
    public List<TourDTO> getToursByGuideName(String guideName) {

        // 1. Шукаємо всіх гідів, які відповідають імені (нечутливо до регістру)
        List<Guide> matchingGuides = guideRepo.findByNameContainingIgnoreCase(guideName);

        if (matchingGuides.isEmpty()) {
            throw new NotFoundServiceException("Guide not found with name: " + guideName);
        }

        // 2. Збираємо ID всіх знайдених гідів
        List<Long> guideIds = matchingGuides.stream()
                .map(Guide::getId)
                .toList();

        // 3. Шукаємо тури, створені цими гідами (потрібен метод findByGuideIdIn у TourRepo!)
        List<Tour> toursByGuides = tourRepo.findByGuideIdIn(guideIds);

        if (toursByGuides.isEmpty()) {
            // Якщо гідів знайшли, але вони не створили турів
            throw new NotFoundServiceException("No Tours found for guide name " + guideName);
        }

        return toursByGuides.stream()
                .map(tourMapper::tourToTourDTO)
                .toList();
    }

    @Override
    public List<TourDTO> getToursByGuideId(Long guideId) {
        // Перевірка існування гіда (якщо потрібно)
        guideRepo.findById(guideId)
                .orElseThrow(() -> new NotFoundServiceException("Guide not found with id: " + guideId));

        // TourRepo повинен мати метод для пошуку за guideId
        List<Tour> toursByGuideId = tourRepo.findToursByGuideId(guideId); // Припускаємо, що метод існує

        if (toursByGuideId.isEmpty()) {
            // Якщо турів немає, кидаємо 404, який Dashboard обробить як "No tours created"
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
                .limit(5)
                .toList();
    }

    //25
    @Override
    @PreAuthorize("@accessChecker.isTourOwner(#id) || hasRole('ADMIN')")
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

    @Override
    public TourDTO getTourById(Long id) {
        Tour tour = tourRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Tour not found with id: " + id));
        return tourMapper.tourToTourDTO(tour);
    }
}
