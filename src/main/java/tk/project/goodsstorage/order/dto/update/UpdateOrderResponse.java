package tk.project.goodsstorage.order.dto.update;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderResponse {

    UUID id;

    String deliveryAddress;

    Collection<UpdateOrderProductResponse> products;
}
