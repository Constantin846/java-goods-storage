package tk.project.goodsstorage.order.dto.update;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import tk.project.goodsstorage.order.model.OrderStatus;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderStatusDto {

    OrderStatus status;
}
