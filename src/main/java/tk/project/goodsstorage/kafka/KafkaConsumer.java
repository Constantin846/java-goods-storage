package tk.project.goodsstorage.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.dto.kafka.event.EventSource;
import tk.project.goodsstorage.dto.kafka.event.order.OrderEventData;
import tk.project.goodsstorage.enums.Event;
import tk.project.goodsstorage.exceptionhandler.exceptions.kafka.EventHandlerNotFoundException;
import tk.project.goodsstorage.exceptionhandler.exceptions.kafka.KafkaConsumerJsonProcessingFoundException;
import tk.project.goodsstorage.services.handlers.EventHandler;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "kafka.enabled", matchIfMissing = false)
public class KafkaConsumer {

    private final Map<Event, EventHandler<EventSource>> eventHandlers;

    @KafkaListener(topics = "${app.kafka.order-command-topic}", containerFactory = "kafkaListenerContainerFactoryString")
    public void listen(final String message) throws JsonProcessingException {
        log.info("Receive message: {}", message);

        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            final OrderEventData eventSource = objectMapper.readValue(message, OrderEventData.class);
            log.info("EventSource: {}", eventSource);


            if (!eventHandlers.containsKey(eventSource.getEvent())) {
                String msg = String.format("Handler for eventsource not found for: %s", eventSource.getEvent());
                log.warn(msg);
                throw new EventHandlerNotFoundException(msg);
            }

            eventHandlers.get(eventSource.getEvent()).handleEvent(eventSource);

        } catch (JsonProcessingException e) {
            String msg = String.format("Couldn't parse message: %s; exception: %s", message, e);
            log.warn(msg);
            throw new KafkaConsumerJsonProcessingFoundException(msg);
        }
    }
}
