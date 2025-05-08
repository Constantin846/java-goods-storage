package tk.project.goodsstorage.dto.order.update;

import tk.project.goodsstorage.dto.order.SaveOrderedProductDto;

import java.util.UUID;

public class UpdateOrderedProductDto extends SaveOrderedProductDto {

    public UpdateOrderedProductDto(final UUID id, final Long count) {
        super(id, count);
    }
}
