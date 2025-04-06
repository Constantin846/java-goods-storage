package tk.project.goodsstorage.order.consuming.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.kafka.event.Event;
import tk.project.goodsstorage.kafka.handler.EventHandler;
import tk.project.goodsstorage.order.consuming.events.CreateOrderEventData;
import tk.project.goodsstorage.order.service.OrderService;

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
    public void handleEvent(CreateOrderEventData eventSource) {
        orderService.create(eventSource.getOrderDto());
        log.info("Create order event has been handled successfully: {}", eventSource);
    }
}
