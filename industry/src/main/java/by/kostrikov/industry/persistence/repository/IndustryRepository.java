package by.kostrikov.industry.persistence.repository;

import by.kostrikov.industry.persistence.model.Industry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IndustryRepository extends JpaRepository<Industry, Long> {
    Optional<Industry> findByMessageId(UUID messageId);
}
