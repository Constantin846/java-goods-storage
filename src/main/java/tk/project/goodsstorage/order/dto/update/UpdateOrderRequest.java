package tk.project.goodsstorage.order.dto.update;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import tk.project.goodsstorage.validation.NullOrNotBlank;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderRequest {

    @NullOrNotBlank(message = "Order delivery address must be null or not blank")
    @Length(message = "Order delivery address length must be between 1 and 128 characters inclusive", min = 1, max = 128)
    String deliveryAddress;

    Set<UpdateOrderProductRequest> products;
}
