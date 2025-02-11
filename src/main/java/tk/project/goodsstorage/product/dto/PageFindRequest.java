package tk.project.goodsstorage.product.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageFindRequest {

    @PositiveOrZero(message = "From param must not be negative")
    int from = 0;

    @Positive(message = "From param must be positive")
    int size = 10;
}
