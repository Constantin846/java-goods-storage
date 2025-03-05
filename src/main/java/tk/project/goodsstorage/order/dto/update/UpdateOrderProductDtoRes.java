package tk.project.goodsstorage.order.dto.update;

import lombok.NoArgsConstructor;
import tk.project.goodsstorage.order.dto.OrderProductDto;

import java.util.UUID;

@NoArgsConstructor
public class UpdateOrderProductDtoRes extends OrderProductDto {
    public UpdateOrderProductDtoRes(UUID id, Long count) {
        this.id = id;
        this.count = count;
    }
}
