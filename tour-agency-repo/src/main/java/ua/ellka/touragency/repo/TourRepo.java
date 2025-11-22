package ua.ellka.touragency.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.ellka.touragency.model.Tour;

import java.util.List;
import java.util.Optional;

public interface TourRepo extends JpaRepository<Tour, Long> {
    List<Tour> findToursByCountryName(String countryName);
    List<Tour> findToursByGuideName(String guideName);
    List<Tour> findToursByGuideId(Long guideId);
    Optional<Tour> findByName(String name);
    List<Tour> findByGuideIdIn(List<Long> guideIds);
}
