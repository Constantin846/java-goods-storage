package tk.project.goodsstorage.order.dto.find;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FindOrderProductDto {

    UUID productId;

    String name;

    BigDecimal price;

    Long count;
}
