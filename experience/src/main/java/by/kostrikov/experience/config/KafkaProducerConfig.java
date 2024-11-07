package by.kostrikov.experience.config;

import by.kostrikov.ws.core.CreateIndustryEvent;
import by.kostrikov.ws.core.ResponseIndustryEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Autowired
    private Environment environment;

    // Получение настроек Kafka из конфигурации
    private Map<String, Object> producerConfigs() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, environment.getProperty("spring.kafka.producer.acks"));
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, environment.getProperty("spring.kafka.producer.properties.delivery.timeout.ms"));
        config.put(ProducerConfig.LINGER_MS_CONFIG, environment.getProperty("spring.kafka.producer.properties.linger.ms"));
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, environment.getProperty("spring.kafka.producer.properties.request.timeout.ms"));
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, environment.getProperty("spring.kafka.producer.properties.enable.idempotence"));
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, environment.getProperty("spring.kafka.producer.properties.max.in.flight.requests.per.connection"));
        config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, environment.getProperty("spring.kafka.producer.transaction-id-prefix"));

        return config;
    }

    @Bean
    public KafkaTransactionManager<String, CreateIndustryEvent> kafkaTransactionManager(
            @Qualifier("createIndustryProducerFactory") ProducerFactory<String, CreateIndustryEvent> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    // Producer для CreateIndustryEvent
    @Bean
    public ProducerFactory<String, CreateIndustryEvent> createIndustryProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, CreateIndustryEvent> createIndustryKafkaTemplate() {
        return new KafkaTemplate<>(createIndustryProducerFactory());
    }

    // Producer для ResponseIndustryEvent
    @Bean
    public ProducerFactory<String, ResponseIndustryEvent> responseIndustryProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, ResponseIndustryEvent> responseIndustryKafkaTemplate() {
        return new KafkaTemplate<>(responseIndustryProducerFactory());
    }

    // Создание Kafka Topic для запросов на создание индустрии
    @Bean
    public NewTopic createProducerTopic() {
        return TopicBuilder.name(environment.getProperty("kafka.producer.create.request.topic.name", "create-request-industry-topic"))
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }
}
