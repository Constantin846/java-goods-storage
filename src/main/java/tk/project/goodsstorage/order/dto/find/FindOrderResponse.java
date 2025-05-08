package tk.project.goodsstorage.order.dto.find;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class FindOrderResponse {

    private final UUID orderId;

    @ToString.Exclude
    private final List<FindOrderedProductResponse> products;

    private final BigDecimal totalPrice;

    private final LocalDate deliveryDate;

    public List<FindOrderedProductResponse> getProducts() {
        return new ArrayList<>(products);
    }
}
