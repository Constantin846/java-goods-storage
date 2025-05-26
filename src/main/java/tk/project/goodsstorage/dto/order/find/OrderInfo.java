package tk.project.goodsstorage.dto.order.find;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.dto.customer.find.CustomerInfo;
import tk.project.goodsstorage.enums.OrderStatus;

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
