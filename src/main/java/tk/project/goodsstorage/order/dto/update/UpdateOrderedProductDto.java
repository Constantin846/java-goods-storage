package tk.project.goodsstorage.order.dto.update;

import tk.project.goodsstorage.order.dto.SaveOrderedProductDto;

import java.util.UUID;

public class UpdateOrderedProductDto extends SaveOrderedProductDto {

    public UpdateOrderedProductDto(final UUID id, final Long count) {
        super(id, count);
    }
}
