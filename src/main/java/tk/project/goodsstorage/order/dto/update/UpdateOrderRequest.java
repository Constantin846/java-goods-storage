package tk.project.goodsstorage.order.dto.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import tk.project.goodsstorage.validation.NullOrNotBlank;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateOrderRequest {

    @NullOrNotBlank(message = "Order delivery address must be null or not blank")
    @Length(message = "Order delivery address length must be between 1 and 128 characters inclusive", min = 1, max = 128)
    private final String deliveryAddress;

    private final Set<UpdateOrderedProductRequest> products;

    public Set<UpdateOrderedProductRequest> getProducts() {
        return new HashSet<>(products);
    }
}
