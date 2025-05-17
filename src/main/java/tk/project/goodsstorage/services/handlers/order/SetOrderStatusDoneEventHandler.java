package tk.project.goodsstorage.services.handlers.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.dto.kafka.event.order.SetOrderStatusDoneEventData;
import tk.project.goodsstorage.enums.Event;
import tk.project.goodsstorage.services.handlers.EventHandler;
import tk.project.goodsstorage.services.order.OrderService;

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
    public void handleEvent(final SetOrderStatusDoneEventData eventSource) {
        orderService.setStatusDone(eventSource.getOrderId());
        log.info("Set order status DONE event has been handled successfully: {}", eventSource);
    }
}
