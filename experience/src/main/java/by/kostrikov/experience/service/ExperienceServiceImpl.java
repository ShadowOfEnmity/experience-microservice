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
        // 1. Создаем IndustryRequestDto и отправляем его через Kafka
        String messageId = UUID.randomUUID().toString();

        CreateIndustryEvent createIndustryEvent = new CreateIndustryEvent();
        createIndustryEvent.setName(requestDto.getIndustry().getName());

        CompletableFuture<ResponseIndustryEvent> futureResponse = new CompletableFuture<>();
        pendingIndustryResponses.put(messageId, futureResponse);

        ProducerRecord<String, CreateIndustryEvent> record = new ProducerRecord<>(
                "${kafka.producer.create.request.topic.name}",
                messageId,
                createIndustryEvent
        );
        record.headers().add("messageId", messageId.getBytes());

        kafkaTemplate.send(record);

        // 2. Ждем асинхронного ответа от Kafka
        ResponseIndustryEvent industryResponse = futureResponse.join();

        IndustryResponseDto industryResponseDto = new IndustryResponseDto();
        industryResponseDto.setId(industryResponse.getId());
        industryResponseDto.setName(industryResponse.getName());

        // 3. Сохраняем Experience с полученным industryId
        Experience experience = experienceMapper.toEntity(requestDto);
        experience.setIndustryId(industryResponseDto.getId());
        Experience savedExperience = experienceRepository.save(experience);

        // 4. Возвращаем DTO ответа
        ExperienceResponseDto response = experienceMapper.toResponseDto(savedExperience);
        response.setIndustry(industryResponseDto);
        return response;
    }

    @KafkaListener(topics = "${kafka.response.topic.name}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void handleIndustryResponse(@Header("messageId") String messageId, ResponseIndustryEvent industryResponse) {
        // Проверка на идемпотентность: обработка только неповторяющихся сообщений
        if (!processedMessageIds.add(messageId)) {
            return;
        }

        CompletableFuture<ResponseIndustryEvent> futureResponse = pendingIndustryResponses.remove(messageId);
        if (futureResponse != null) {
            futureResponse.complete(industryResponse);
        }
    }
}
