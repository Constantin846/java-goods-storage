package tk.project.goodsstorage.order.consuming.events;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.kafka.event.Event;
import tk.project.goodsstorage.order.dto.create.CreateOrderDto;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateOrderEventData implements OrderEventData {

    private final CreateOrderDto orderDto;

    private final Event event;
}
