package tk.project.goodsstorage.order.dto.update;

import lombok.Data;
import tk.project.goodsstorage.order.model.OrderStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class SetOrderStatusRequest {

    private UUID orderId;

    private OrderStatus status;

    private LocalDate deliveryDate;
}
