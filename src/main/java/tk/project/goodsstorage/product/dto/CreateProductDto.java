package tk.project.goodsstorage.product.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductDto {

    String name;

    String article;

    String description;

    String category;

    Double price;

    Long count;
}
