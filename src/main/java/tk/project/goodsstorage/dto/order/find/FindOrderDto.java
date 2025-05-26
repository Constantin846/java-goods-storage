package tk.project.goodsstorage.dto.order.find;

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
public class FindOrderDto {

    private final UUID orderId;

    @ToString.Exclude
    private final List<FindOrderedProductDto> products;

    private final BigDecimal totalPrice;

    private final LocalDate deliveryDate;

    public List<FindOrderedProductDto> getProducts() {
        return new ArrayList<>(products);
    }
}
