package tk.project.goodsstorage.dto.order.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateOrderDto {

    private final UUID id;

    private final String deliveryAddress;

    @ToString.Exclude
    private final Set<UpdateOrderedProductDto> products;

    public Set<UpdateOrderedProductDto> getProducts() {
        return new HashSet<>(products);
    }
}
