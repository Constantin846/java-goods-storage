package tk.project.goodsstorage.order.consuming.events;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import tk.project.goodsstorage.kafka.event.Event;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteOrderEventData implements OrderEventData {

    UUID orderId;

    Long customerId;

    Event event;
}
