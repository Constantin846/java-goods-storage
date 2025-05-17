package tk.project.goodsstorage.dto.order.create;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tk.project.goodsstorage.dto.order.SaveOrderedProductDto;

import java.util.UUID;

public class CreateOrderedProductDto extends SaveOrderedProductDto {

    @JsonCreator
    public CreateOrderedProductDto(@JsonProperty("id") final UUID id,
                                   @JsonProperty("count") final Long count) {
        super(id, count);
    }
}
