package tk.project.goodsstorage.product.dto.find;

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
@EqualsAndHashCode(of = "id")
public class ProductResponse {

    private final UUID id;

    private final String name;

    private final String description;

    private final String category;

    private final BigDecimal price;

    private final Long count;

    private final Instant lastCountUpdateTime;

    private final LocalDate createDate;

    private final Boolean isAvailable;

    private final String currency;
}
