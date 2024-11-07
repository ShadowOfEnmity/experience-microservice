package by.kostrikov.industry.service;

import by.kostrikov.industry.dto.IndustryDto;
import by.kostrikov.industry.persistence.model.Industry;
import by.kostrikov.ws.core.CreateIndustryEvent;
import by.kostrikov.ws.core.ResponseIndustryEvent;

import java.util.Optional;
import java.util.UUID;

public interface IndustryService {
    ResponseIndustryEvent createIndustry(CreateIndustryEvent industry, UUID messageId);
    Optional<Industry> findIndustryByMessageId(UUID messageId);
}
