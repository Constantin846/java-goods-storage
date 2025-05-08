package tk.project.goodsstorage.order.dto.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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
}
