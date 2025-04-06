package tk.project.goodsstorage.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tk.project.goodsstorage.kafka.event.EventSource;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "app", name = "kafka.enabled", matchIfMissing = false)
public class KafkaProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplateByteArray;
    public static final String ORDER_COMMAND = "brokerage-order-command";

    public KafkaProducer(@Autowired KafkaTemplate<String, byte[]> kafkaTemplateByteArray) {
        this.kafkaTemplateByteArray = kafkaTemplateByteArray;
    }

    public void sendEvent(final String topic, final String key, final EventSource event) {
        Assert.hasText(topic, "topic must not be blank");
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(event, "KafkaEvent must not be null");

        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonString;

        try {
            jsonString = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("ObjectMapper write value as string exception");
            throw new RuntimeException(e);
        }

        byte[] data = jsonString.getBytes();

        try {
            kafkaTemplateByteArray.send(topic, key, data).get(1L, TimeUnit.MINUTES);
            log.info("Kafka send complete");

        } catch (Exception e) {
            log.error("Kafka fail send: {}", e.getMessage());
        }
    }
}