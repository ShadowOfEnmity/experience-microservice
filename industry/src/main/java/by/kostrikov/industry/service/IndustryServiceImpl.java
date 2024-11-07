package by.kostrikov.industry.service;

import by.kostrikov.industry.persistence.model.Industry;
import by.kostrikov.industry.persistence.repository.IndustryRepository;
import by.kostrikov.ws.core.CreateIndustryEvent;
import by.kostrikov.ws.core.ResponseIndustryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class IndustryServiceImpl implements IndustryService {
    private final IndustryRepository industryRepository;

    @Autowired
    public IndustryServiceImpl(IndustryRepository industryRepository) {
        this.industryRepository = industryRepository;
    }

    @Override
    public ResponseIndustryEvent createIndustry(CreateIndustryEvent industryEvent, UUID messageId) {
        Industry industry = new Industry();
        industry.setName(industryEvent.getName());
        industry.setMessageId(messageId);
        Industry savedIndustry = industryRepository.save(industry);
        return  new ResponseIndustryEvent(savedIndustry.getId(), savedIndustry.getName());
    }

    @Override
    public Optional<Industry> findIndustryByMessageId(UUID messageId) {
        return industryRepository.findByMessageId(messageId);
    }

}
