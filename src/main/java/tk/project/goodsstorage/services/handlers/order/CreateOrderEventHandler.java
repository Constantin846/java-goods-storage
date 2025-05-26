package tk.project.goodsstorage.services.handlers.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.dto.kafka.event.order.CreateOrderEventData;
import tk.project.goodsstorage.enums.Event;
import tk.project.goodsstorage.services.handlers.EventHandler;
import tk.project.goodsstorage.services.order.OrderService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderEventHandler implements EventHandler<CreateOrderEventData> {
    private static final Event EVENT = Event.CREATE_ORDER;
    private final OrderService orderService;

    @Override
    public Event getEvent() {
        return EVENT;
    }

    @Override
    public void handleEvent(final CreateOrderEventData eventSource) {
        orderService.create(eventSource.getOrderDto());
        log.info("Create order event has been handled successfully: {}", eventSource);
    }
}
