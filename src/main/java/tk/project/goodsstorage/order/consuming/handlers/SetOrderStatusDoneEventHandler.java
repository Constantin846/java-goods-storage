package tk.project.goodsstorage.order.consuming.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.kafka.event.Event;
import tk.project.goodsstorage.kafka.handler.EventHandler;
import tk.project.goodsstorage.order.consuming.events.SetOrderStatusDoneEventData;
import tk.project.goodsstorage.order.service.OrderService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetOrderStatusDoneEventHandler implements EventHandler<SetOrderStatusDoneEventData> {
    private static final Event EVENT = Event.SET_ORDER_STATUS_DONE;
    private final OrderService orderService;

    @Override
    public Event getEvent() {
        return EVENT;
    }

    @Override
    public void handleEvent(SetOrderStatusDoneEventData eventSource) {
        orderService.setStatusDone(eventSource.getOrderId(), eventSource.getCustomerId());
        log.info("Set order status DONE event has been handled successfully: {}", eventSource);
    }
}
