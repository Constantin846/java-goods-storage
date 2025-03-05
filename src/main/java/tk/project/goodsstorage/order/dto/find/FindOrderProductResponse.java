package tk.project.goodsstorage.order.dto.find;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FindOrderProductResponse {

    UUID productId;

    String name;

    BigDecimal price;

    Long count;
}
