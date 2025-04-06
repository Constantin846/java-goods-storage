package tk.project.goodsstorage.order.consuming.events;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import tk.project.goodsstorage.kafka.event.Event;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDto;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderEventData implements OrderEventData {

    UpdateOrderDto orderDto;

    Event event;
}
