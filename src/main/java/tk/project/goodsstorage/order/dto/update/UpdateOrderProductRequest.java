package tk.project.goodsstorage.order.dto.update;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderProductRequest {

    @NotNull(message = "Products' ids must be set")
    UUID id;

    @NotNull(message = "Products' counts must be set")
    @Positive(message = "Products' counts must be positive")
    Long count;
}
