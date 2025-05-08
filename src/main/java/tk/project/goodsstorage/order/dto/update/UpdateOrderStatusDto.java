package tk.project.goodsstorage.order.dto.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.order.model.OrderStatus;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UpdateOrderStatusDto {

    private final OrderStatus status;
}
