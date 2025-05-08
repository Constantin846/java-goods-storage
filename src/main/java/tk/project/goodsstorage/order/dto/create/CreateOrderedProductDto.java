package tk.project.goodsstorage.order.dto.create;

import tk.project.goodsstorage.order.dto.SaveOrderedProductDto;

import java.util.UUID;

public class CreateOrderedProductDto extends SaveOrderedProductDto {

    public CreateOrderedProductDto(final UUID id, final Long count) {
        super(id, count);
    }
}
