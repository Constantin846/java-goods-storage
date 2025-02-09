package tk.project.goodsstorage.product.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

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

    Integer price;

    Long count;

    Instant lastCountUpdateTime;

    LocalDate createDate;
}
