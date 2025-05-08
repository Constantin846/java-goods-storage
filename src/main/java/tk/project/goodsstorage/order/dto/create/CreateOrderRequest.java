package tk.project.goodsstorage.order.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateOrderRequest {

    @NotBlank(message = "Order delivery address must be set")
    @Length(message = "Order delivery address length must be between 1 and 128 characters inclusive", min = 1, max = 128)
    private final String deliveryAddress;

    @NotNull(message = "Order products must be set")
    @NotEmpty(message = "Order products must have at least one product")
    @ToString.Exclude
    private final Set<CreateOrderedProductRequest> products;

    public Set<CreateOrderedProductRequest> getProducts() {
        return new HashSet<>(products);
    }
}
