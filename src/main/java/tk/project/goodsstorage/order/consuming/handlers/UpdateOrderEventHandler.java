package tk.project.goodsstorage.order.consuming.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.kafka.event.Event;
import tk.project.goodsstorage.kafka.handler.EventHandler;
import tk.project.goodsstorage.order.consuming.events.UpdateOrderEventData;
import tk.project.goodsstorage.order.service.OrderService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateOrderEventHandler implements EventHandler<UpdateOrderEventData> {
    private static final Event EVENT = Event.UPDATE_ORDER;
    private final OrderService orderService;

    public Event getEvent() {
        return EVENT;
    }

    @Override
    public void handleEvent(final UpdateOrderEventData eventSource) {
        orderService.update(eventSource.getOrderDto(), eventSource.getOrderDto().getId());
        log.info("Update order event has been handled successfully: {}", eventSource);
    }
}
