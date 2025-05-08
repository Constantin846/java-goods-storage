package tk.project.goodsstorage.order.info;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.customer.dto.find.CustomerInfo;
import tk.project.goodsstorage.order.model.OrderStatus;

import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class OrderInfo {

    private final UUID id;

    private final CustomerInfo customer;

    private final OrderStatus status;

    private final String deliveryAddress;

    private final Long count;
}
