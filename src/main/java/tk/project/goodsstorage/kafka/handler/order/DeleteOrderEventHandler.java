package tk.project.goodsstorage.kafka.handler.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.kafka.event.Event;
import tk.project.goodsstorage.kafka.event.order.DeleteOrderEventData;
import tk.project.goodsstorage.kafka.handler.EventHandler;
import tk.project.goodsstorage.services.order.OrderService;

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
    public void handleEvent(final DeleteOrderEventData eventSource) {
        orderService.deleteById(eventSource.getOrderId());
        log.info("Delete order event has been handled successfully: {}", eventSource);
    }
}
