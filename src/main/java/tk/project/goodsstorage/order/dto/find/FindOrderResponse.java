package tk.project.goodsstorage.order.dto.find;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FindOrderResponse {

    UUID orderId;

    List<FindOrderedProductResponse> products;

    BigDecimal totalPrice;

    LocalDate deliveryDate;
}
