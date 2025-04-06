package tk.project.goodsstorage.order.info;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import tk.project.goodsstorage.customer.dto.find.CustomerInfo;
import tk.project.goodsstorage.order.model.OrderStatus;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderInfo {

    UUID id;

    CustomerInfo customer;

    OrderStatus status;

    String deliveryAddress;

    Long count;
}
