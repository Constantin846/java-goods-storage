package tk.project.goodsstorage.dto.kafka.event.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.dto.order.update.UpdateOrderDto;
import tk.project.goodsstorage.enums.Event;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateOrderEventData implements OrderEventData {

    private final UpdateOrderDto orderDto;

    private final Event event;

    @JsonCreator
    public UpdateOrderEventData(@JsonProperty("orderDto") final UpdateOrderDto orderDto,
                                @JsonProperty("event") final Event event) {
        this.orderDto = orderDto;
        this.event = event;
    }
}
