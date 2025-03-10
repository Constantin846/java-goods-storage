package tk.project.goodsstorage.product.dto.create;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductDto {

    String name;

    String article;

    String description;

    String category;

    BigDecimal price;

    Long count;

    Boolean isAvailable;
}
