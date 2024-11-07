package by.kostrikov.industry.handler;

import by.kostrikov.industry.persistence.model.Industry;
import by.kostrikov.industry.service.IndustryService;
import by.kostrikov.ws.core.CreateIndustryEvent;
import by.kostrikov.ws.core.ResponseIndustryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class IndustryKafkaListener {
    private final IndustryService industryService;
    private final KafkaTemplate<String, ResponseIndustryEvent> kafkaProducerTemplate;

    @Value("${kafka.response.topic.name}")
    private String responseTopicName;

    public IndustryKafkaListener(@Qualifier("industryServiceImpl") IndustryService industryService, KafkaTemplate<String, ResponseIndustryEvent> kafkaProducerTemplate) {
        this.industryService = industryService;
        this.kafkaProducerTemplate = kafkaProducerTemplate;
    }

    @Transactional("transactionManager")
    @KafkaListener(topics = "${kafka.producer.create.request.topic.name}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void listenIndustry(@Payload CreateIndustryEvent CreateIndustryEvent, @Header(KafkaHeaders.RECEIVED_KEY) String key, @Header("messageId") String messageId) {

        UUID messageIdUUID = UUID.fromString(messageId);

        Optional<Industry> industryByMessageId = industryService.findIndustryByMessageId(messageIdUUID);
        if (industryByMessageId.isPresent()) {
            log.info("Duplicate message id: {}", messageId);
            return;
        }

        ResponseIndustryEvent createdIndustry = industryService.createIndustry(CreateIndustryEvent, messageIdUUID);

        ProducerRecord<String, ResponseIndustryEvent> record = new ProducerRecord<>(
                responseTopicName,
                messageId,
                createdIndustry
        );
        record.headers().add("messageId", messageId.getBytes());

        kafkaProducerTemplate.send(record);
    }

}
