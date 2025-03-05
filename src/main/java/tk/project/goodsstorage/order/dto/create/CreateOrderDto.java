package tk.project.goodsstorage.order.dto.create;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import tk.project.goodsstorage.order.model.Status;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderDto {

    Long customerId;

    Status status = Status.CREATED;

    String deliveryAddress;

    Set<CreateOrderProductDto> products;
}
