package tk.project.goodsstorage.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {

    @NotBlank(message = "Product name must be set")
    String name;

    @NotBlank(message = "Product article must be set")
    String article;

    @NotBlank(message = "Product description must be set")
    String description;

    @NotBlank(message = "Product category must be set")
    String category;

    @NotNull(message = "Product price must be set")
    @PositiveOrZero(message = "Product price must be positive or zero")
    Integer price;

    @NotNull(message = "Product count must be set")
    Long count;
}
