package tk.project.goodsstorage.order.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class SaveOrderedProductDto {

    UUID id;

    Long count;
}
