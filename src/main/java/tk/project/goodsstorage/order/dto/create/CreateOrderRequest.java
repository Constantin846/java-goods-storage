package tk.project.goodsstorage.order.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderRequest {

    @NotBlank(message = "Order delivery address must be set")
    @Length(message = "Order delivery address length must be between 1 and 128 characters inclusive", min = 1, max = 128)
    String deliveryAddress;

    @NotNull(message = "Order products must be set")
    @NotEmpty(message = "Order products must have at least one product")
    Set<CreateOrderProductRequest> products;
}
