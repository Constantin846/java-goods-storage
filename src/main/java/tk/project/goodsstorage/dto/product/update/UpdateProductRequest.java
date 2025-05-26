package tk.project.goodsstorage.dto.product.update;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import tk.project.goodsstorage.annotations.NullOrNotBlank;

import java.math.BigDecimal;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateProductRequest {

    @NullOrNotBlank(message = "Product name must be null or not blank")
    @Length(message = "Product name length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    private final String name;

    @NullOrNotBlank(message = "Product article must be null or not blank")
    @Length(message = "Product article length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    private final String article;

    @NullOrNotBlank(message = "Product description must be null or not blank")
    private final String description;

    @NullOrNotBlank(message = "Product category must be null or not blank")
    @Length(message = "Product category length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    private final String category;

    @PositiveOrZero(message = "Product price must be positive or zero")
    private final BigDecimal price;

    private final Long count;

    private final Boolean isAvailable;
}
