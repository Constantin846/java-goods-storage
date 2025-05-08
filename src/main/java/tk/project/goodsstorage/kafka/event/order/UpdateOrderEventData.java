package tk.project.goodsstorage.kafka.event.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.dto.order.update.UpdateOrderDto;
import tk.project.goodsstorage.kafka.event.Event;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateOrderEventData implements OrderEventData {

    private final UpdateOrderDto orderDto;

    private final Event event;
}
