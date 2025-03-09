package tk.project.goodsstorage.order.dto.update;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import tk.project.goodsstorage.customer.Customer;

import java.util.Collection;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderDtoRes {

    UUID id;

    Customer customer;

    String deliveryAddress;

    Collection<UpdateOrderedProductDtoRes> products;
}
