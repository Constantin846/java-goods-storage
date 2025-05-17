package tk.project.goodsstorage.dto.order.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public UpdateOrderDto(@JsonProperty("id") final UUID id,
                          @JsonProperty("deliveryAddress") final String deliveryAddress,
                          @JsonProperty("products") final Set<UpdateOrderedProductDto> products) {
        this.id = id;
        this.deliveryAddress = deliveryAddress;
        this.products = products;
    }

    public Set<UpdateOrderedProductDto> getProducts() {
        return new HashSet<>(products);
    }
}
