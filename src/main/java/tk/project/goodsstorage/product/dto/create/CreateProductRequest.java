package tk.project.goodsstorage.product.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

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

    @Builder.Default
    private final Boolean isAvailable = true;
}
