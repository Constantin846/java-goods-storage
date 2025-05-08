package tk.project.goodsstorage.dto.order.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.models.Customer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateOrderDtoRes {

    private final UUID id;

    private final Customer customer;

    private final String deliveryAddress;

    private final Collection<UpdateOrderedProductDtoRes> products;

    public Collection<UpdateOrderedProductDtoRes> getProducts() {
        return new ArrayList<>(products);
    }
}
