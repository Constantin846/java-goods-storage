package tk.project.goodsstorage.dto.product.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateProductRequest {

    @NotBlank(message = "Product name must be set")
    @Length(message = "Product name length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    private final String name;

    @NotBlank(message = "Product article must be set")
    @Length(message = "Product article length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    private final String article;

    @NotBlank(message = "Product description must be set")
    private final String description;

    @NotBlank(message = "Product category must be set")
    @Length(message = "Product category length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    private final String category;

    @NotNull(message = "Product price must be set")
    @PositiveOrZero(message = "Product price must be positive or zero")
    private final BigDecimal price;

    @NotNull(message = "Product count must be set")
    private final Long count;

    private final Boolean isAvailable;

    private static final boolean IS_AVAILABLE_DEFAULT = true;

    public CreateProductRequest(
            String name,
            String article,
            String description,
            String category,
            BigDecimal price,
            Long count,
            Boolean isAvailable
    ) {
        this.name = name;
        this.article = article;
        this.description = description;
        this.category = category;
        this.price = price;
        this.count = count;
        this.isAvailable = Objects.isNull(isAvailable) ? IS_AVAILABLE_DEFAULT : isAvailable;
    }
}
