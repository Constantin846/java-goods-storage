package tk.project.goodsstorage.kafka.event.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.kafka.event.Event;

import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class DeleteOrderEventData implements OrderEventData {

    private final UUID orderId;

    private final Long customerId;

    private final Event event;
}
