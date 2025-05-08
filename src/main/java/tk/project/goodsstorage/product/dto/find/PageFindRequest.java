package tk.project.goodsstorage.product.dto.find;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class PageFindRequest {

    @PositiveOrZero(message = "From param must not be negative")
    private final int from = 0;

    @Positive(message = "From param must be positive")
    private final int size = 10;
}
