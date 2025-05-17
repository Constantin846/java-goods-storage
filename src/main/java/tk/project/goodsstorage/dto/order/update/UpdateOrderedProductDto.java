package tk.project.goodsstorage.dto.order.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tk.project.goodsstorage.dto.order.SaveOrderedProductDto;

import java.util.UUID;

public class UpdateOrderedProductDto extends SaveOrderedProductDto {

    @JsonCreator
    public UpdateOrderedProductDto(@JsonProperty("id") final UUID id,
                                   @JsonProperty("count") final Long count) {
        super(id, count);
    }
}
