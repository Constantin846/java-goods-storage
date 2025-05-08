package tk.project.goodsstorage.order.dto.find;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class FindOrderedProductDto {

    private final UUID productId;

    private final String name;

    private final BigDecimal price;

    private final Long count;
}
