package tk.project.goodsstorage.dto.kafka.event.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.dto.order.create.CreateOrderDto;
import tk.project.goodsstorage.enums.Event;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateOrderEventData implements OrderEventData {

    private final CreateOrderDto orderDto;

    private final Event event;

    @JsonCreator
    public CreateOrderEventData(@JsonProperty("orderDto") final CreateOrderDto orderDto,
                                @JsonProperty("event") final Event event) {
        this.orderDto = orderDto;
        this.event = event;
    }
}
