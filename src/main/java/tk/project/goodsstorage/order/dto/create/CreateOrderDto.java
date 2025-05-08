package tk.project.goodsstorage.order.dto.create;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.order.model.OrderStatus;

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
