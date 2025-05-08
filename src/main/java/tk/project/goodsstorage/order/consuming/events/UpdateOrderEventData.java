package tk.project.goodsstorage.order.consuming.events;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.kafka.event.Event;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDto;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateOrderEventData implements OrderEventData {

    private final UpdateOrderDto orderDto;

    private final Event event;
}
