package ua.ellka.touragency.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.ellka.touragency.model.Country;

import java.util.List;
import java.util.Optional;

public interface CountryRepo extends JpaRepository<Country, Long> {
    Optional<Country> findByNameAndRegion(String name, String region);
    List<Country> findByName(String name);
}
