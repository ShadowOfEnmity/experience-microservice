package by.kostrikov.experience.service;

import by.kostrikov.experience.dto.ExperienceRequestDto;
import by.kostrikov.experience.dto.ExperienceResponseDto;
import by.kostrikov.experience.dto.IndustryResponseDto;
import by.kostrikov.experience.persistence.ExperienceRepository;
import by.kostrikov.experience.persistence.model.Experience;
import by.kostrikov.experience.utils.ExperienceMapper;
import by.kostrikov.ws.core.CreateIndustryEvent;
import by.kostrikov.ws.core.ResponseIndustryEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;


@Service
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ExperienceMapper experienceMapper;
    private final KafkaTemplate<String, CreateIndustryEvent> kafkaTemplate;
    @Value("${kafka.producer.create.request.topic.name}")
    private String createTopicName;

    private final ConcurrentHashMap<String, CompletableFuture<ResponseIndustryEvent>> pendingIndustryResponses = new ConcurrentHashMap<>();
    private final ConcurrentSkipListSet<String> processedMessageIds = new ConcurrentSkipListSet<>();


    public ExperienceServiceImpl(ExperienceRepository experienceRepository,
                                 ExperienceMapper experienceMapper,
                                 KafkaTemplate<String, CreateIndustryEvent> kafkaTemplate) {
        this.experienceRepository = experienceRepository;
        this.experienceMapper = experienceMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @Override
    public ExperienceResponseDto saveExperience(ExperienceRequestDto requestDto) {
        String messageId = UUID.randomUUID().toString();

        CreateIndustryEvent createIndustryEvent = new CreateIndustryEvent();
        createIndustryEvent.setName(requestDto.getIndustry().getName());

        CompletableFuture<ResponseIndustryEvent> futureResponse = new CompletableFuture<>();
        pendingIndustryResponses.put(messageId, futureResponse);

        ProducerRecord<String, CreateIndustryEvent> record = new ProducerRecord<>(
                createTopicName,
                messageId,
                createIndustryEvent
        );
        record.headers().add("messageId", messageId.getBytes());

        kafkaTemplate.send(record);

        ResponseIndustryEvent industryResponse = futureResponse.join();

        IndustryResponseDto industryResponseDto = new IndustryResponseDto();
        industryResponseDto.setId(industryResponse.getId());
        industryResponseDto.setName(industryResponse.getName());

        Experience experience = experienceMapper.toEntity(requestDto);
        experience.setIndustryId(industryResponseDto.getId());
        Experience savedExperience = experienceRepository.save(experience);

        ExperienceResponseDto response = experienceMapper.toResponseDto(savedExperience);
        response.setIndustry(industryResponseDto);
        return response;
    }

    @KafkaListener(topics = "${kafka.response.topic.name}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void handleIndustryResponse(@Header("messageId") String messageId, ResponseIndustryEvent industryResponse) {
        if (!processedMessageIds.add(messageId)) {
            return;
        }

        CompletableFuture<ResponseIndustryEvent> futureResponse = pendingIndustryResponses.remove(messageId);
        if (futureResponse != null) {
            futureResponse.complete(industryResponse);
        }
    }
}
