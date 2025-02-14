package tk.project.goodsstorage.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {

    @NotBlank(message = "Product name must be set")
    @Length(message = "Product name length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    String name;

    @NotBlank(message = "Product article must be set")
    @Length(message = "Product article length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    String article;

    @NotBlank(message = "Product description must be set")
    String description;

    @NotBlank(message = "Product category must be set")
    @Length(message = "Product category length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    String category;

    @NotNull(message = "Product price must be set")
    @PositiveOrZero(message = "Product price must be positive or zero")
    Double price;

    @NotNull(message = "Product count must be set")
    Long count;
}
