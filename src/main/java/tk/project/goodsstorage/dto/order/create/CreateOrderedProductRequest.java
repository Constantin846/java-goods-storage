package tk.project.goodsstorage.dto.order.create;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateOrderedProductRequest {

    @NotNull(message = "Products' ids must be set")
    private final UUID id;

    @NotNull(message = "Products' counts must be set")
    @Positive(message = "Products' counts must be positive")
    private final Long count;
}
