package tk.project.goodsstorage.kafka.event.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.dto.order.create.CreateOrderDto;
import tk.project.goodsstorage.kafka.event.Event;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateOrderEventData implements OrderEventData {

    private final CreateOrderDto orderDto;

    private final Event event;
}
