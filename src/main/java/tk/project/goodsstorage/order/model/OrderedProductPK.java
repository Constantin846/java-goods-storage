package tk.project.goodsstorage.order.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderedProductPK implements Serializable {

    Order order;

    UUID productId;

}
