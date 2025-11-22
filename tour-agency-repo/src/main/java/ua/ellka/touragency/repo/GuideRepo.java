package ua.ellka.touragency.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.ellka.touragency.model.Guide;

import java.util.List;
import java.util.Optional;

public interface GuideRepo extends JpaRepository<Guide, Long> {
    Optional<Guide> findByName(String name);
    Optional<Guide> findByUserId(Long userId);
    List<Guide> findByNameContainingIgnoreCase(String name);
}
