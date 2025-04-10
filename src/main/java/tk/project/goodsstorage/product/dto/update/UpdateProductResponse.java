package tk.project.goodsstorage.product.dto.update;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductResponse {

    UUID id;

    String name;

    String description;

    String category;

    BigDecimal price;

    Long count;

    Instant lastCountUpdateTime;

    LocalDate createDate;

    Boolean isAvailable;
}
