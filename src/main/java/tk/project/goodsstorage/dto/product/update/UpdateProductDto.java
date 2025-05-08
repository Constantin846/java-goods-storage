package tk.project.goodsstorage.dto.product.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateProductDto {

    private final UUID id;

    private final String name;

    private final String article;

    private final String description;

    private final String category;

    private final BigDecimal price;

    private final Long count;

    private final Instant lastCountUpdateTime;

    private final LocalDate createDate;

    private final Boolean isAvailable;
}
