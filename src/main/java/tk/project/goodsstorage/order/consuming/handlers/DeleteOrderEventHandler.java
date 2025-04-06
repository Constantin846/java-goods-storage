package tk.project.goodsstorage.order.consuming.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.kafka.event.Event;
import tk.project.goodsstorage.kafka.handler.EventHandler;
import tk.project.goodsstorage.order.consuming.events.DeleteOrderEventData;
import tk.project.goodsstorage.order.service.OrderService;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteOrderEventHandler implements EventHandler<DeleteOrderEventData> {
    private static final Event EVENT = Event.DELETE_ORDER;
    private final OrderService orderService;

    @Override
    public Event getEvent() {
        return EVENT;
    }

    @Override
    public void handleEvent(DeleteOrderEventData eventSource) {
        orderService.deleteById(eventSource.getOrderId(), eventSource.getCustomerId());
        log.info("Delete order event has been handled successfully: {}", eventSource);
    }
}
