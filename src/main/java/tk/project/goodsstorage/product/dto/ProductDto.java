package tk.project.goodsstorage.product.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import tk.project.goodsstorage.product.currency.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {

    UUID id;

    String name;

    String article;

    String description;

    String category;

    BigDecimal price;

    Long count;

    Instant lastCountUpdateTime;

    LocalDate createDate;

    Boolean isAvailable;

    Currency currency = Currency.RUS;
}
