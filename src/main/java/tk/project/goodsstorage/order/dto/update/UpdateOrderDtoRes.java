package tk.project.goodsstorage.order.dto.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.customer.Customer;

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
}
