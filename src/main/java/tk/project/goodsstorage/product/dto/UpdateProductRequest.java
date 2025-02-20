package tk.project.goodsstorage.product.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import tk.project.goodsstorage.validation.NullOrNotBlank;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductRequest {

    @NullOrNotBlank(message = "Product name must be null or not blank")
    @Length(message = "Product name length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    String name;

    @NullOrNotBlank(message = "Product article must be null or not blank")
    @Length(message = "Product article length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    String article;

    @NullOrNotBlank(message = "Product description must be null or not blank")
    String description;

    @NullOrNotBlank(message = "Product category must be null or not blank")
    @Length(message = "Product category length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    String category;

    @PositiveOrZero(message = "Product price must be positive or zero")
    BigDecimal price;

    Long count;
}
