package tk.project.goodsstorage.dto.order.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateOrderResponse {

    private final UUID id;

    private final String deliveryAddress;

    private final Collection<UpdateOrderedProductResponse> products;

    public Collection<UpdateOrderedProductResponse> getProducts() {
        return new ArrayList<>(products);
    }
}
