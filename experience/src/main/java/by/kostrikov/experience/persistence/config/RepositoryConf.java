package by.kostrikov.experience.persistence.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("by.kostrikov.experience.persistence.model")
@EnableJpaRepositories("by.kostrikov.experience.persistence")
public class RepositoryConf {
}
