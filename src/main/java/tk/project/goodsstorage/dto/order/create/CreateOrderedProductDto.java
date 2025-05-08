package tk.project.goodsstorage.dto.order.create;

import tk.project.goodsstorage.dto.order.SaveOrderedProductDto;

import java.util.UUID;

public class CreateOrderedProductDto extends SaveOrderedProductDto {

    public CreateOrderedProductDto(final UUID id, final Long count) {
        super(id, count);
    }
}
