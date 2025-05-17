package tk.project.goodsstorage.dto.order.create;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public CreateOrderDto(@JsonProperty("deliveryAddress") final String deliveryAddress,
                          @JsonProperty("products") final Set<CreateOrderedProductDto> products) {
        this.deliveryAddress = deliveryAddress;
        this.products = products;
    }

    public Set<CreateOrderedProductDto> getProducts() {
        return new HashSet<>(products);
    }
}
