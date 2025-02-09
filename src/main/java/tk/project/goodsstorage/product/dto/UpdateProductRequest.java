package tk.project.goodsstorage.product.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import tk.project.goodsstorage.validation.NullOrNotBlank;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductRequest {

    @NullOrNotBlank(message = "Product name must be null or not blank")
    String name;

    @NullOrNotBlank(message = "Product article must be null or not blank")
    String article;

    @NullOrNotBlank(message = "Product description must be null or not blank")
    String description;

    @NullOrNotBlank(message = "Product category must be null or not blank")
    String category;

    @PositiveOrZero(message = "Product price must be positive or zero")
    Integer price;

    Long count;
}
