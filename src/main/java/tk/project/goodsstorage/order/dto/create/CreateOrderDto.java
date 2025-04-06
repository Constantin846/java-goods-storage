package tk.project.goodsstorage.order.dto.create;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import tk.project.goodsstorage.order.model.OrderStatus;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderDto {

    Long customerId;

    OrderStatus status = OrderStatus.CREATED;

    String deliveryAddress;

    Set<CreateOrderedProductDto> products;
}
