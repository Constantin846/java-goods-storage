package tk.project.goodsstorage.order.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class OrderProductDto {

    UUID id;

    Long count;
}
