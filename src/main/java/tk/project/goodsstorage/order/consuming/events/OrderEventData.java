package tk.project.goodsstorage.order.consuming.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tk.project.goodsstorage.kafka.event.EventSource;

@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME, include = JsonTypeInfo.As.PROPERTY,
        property = "event", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "CREATE_ORDER", value = CreateOrderEventData.class),
        @JsonSubTypes.Type(name = "UPDATE_ORDER", value = UpdateOrderEventData.class),
        @JsonSubTypes.Type(name = "SET_ORDER_STATUS_DONE", value = SetOrderStatusDoneEventData.class),
        @JsonSubTypes.Type(name = "DELETE_ORDER", value = DeleteOrderEventData.class)
})
public interface OrderEventData extends EventSource {
}
