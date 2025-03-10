package tk.project.goodsstorage.order.dto.update;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderedProductDtoRes {

    UUID productId;

    Long count;
}
