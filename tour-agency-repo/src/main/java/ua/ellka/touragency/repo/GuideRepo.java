package ua.ellka.touragency.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.ellka.touragency.model.Guide;

import java.util.Optional;

public interface GuideRepo extends JpaRepository<Guide, Long> {
    Optional<Guide> findByName(String name);
}
