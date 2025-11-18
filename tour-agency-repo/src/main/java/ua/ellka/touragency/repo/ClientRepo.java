package ua.ellka.touragency.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.ellka.touragency.model.Client;

import java.util.Optional;

public interface ClientRepo extends JpaRepository<Client, Long> {
    Optional<Client> findByPassportNumber(String passportNumber);
    Optional<Client> findByPhone(String phone);
    Optional<Client> findByName(String name);
}
