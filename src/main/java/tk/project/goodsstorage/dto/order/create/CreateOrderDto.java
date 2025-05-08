package tk.project.goodsstorage.dto.order.create;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.enums.OrderStatus;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateOrderDto {

    private final OrderStatus status = OrderStatus.CREATED;

    private final String deliveryAddress;

    @ToString.Exclude
    private final Set<CreateOrderedProductDto> products;

    public Set<CreateOrderedProductDto> getProducts() {
        return new HashSet<>(products);
    }
}
