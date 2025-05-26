package tk.project.goodsstorage.dto.product.create;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateProductDto {

    private final String name;

    private final String article;

    private final String description;

    private final String category;

    private final BigDecimal price;

    private final Long count;

    private final Boolean isAvailable;
}
