package tk.project.goodsstorage.product.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductDto {

    UUID id;

    String name;

    String article;

    String description;

    String category;

    Double price;

    Long count;

    Instant lastCountUpdateTime;

    LocalDate createDate;
}
