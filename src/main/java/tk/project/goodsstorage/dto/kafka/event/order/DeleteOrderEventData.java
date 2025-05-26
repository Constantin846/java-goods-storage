package tk.project.goodsstorage.dto.kafka.event.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.enums.Event;

import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class DeleteOrderEventData implements OrderEventData {

    private final UUID orderId;

    private final Long customerId;

    private final Event event;

    @JsonCreator
    public DeleteOrderEventData(@JsonProperty("orderId") final UUID orderId,
                                @JsonProperty("customerId") final Long customerId,
                                @JsonProperty("event")final Event event) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.event = event;
    }
}
