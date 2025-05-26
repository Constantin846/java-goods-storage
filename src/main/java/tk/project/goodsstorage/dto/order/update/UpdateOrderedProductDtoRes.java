package tk.project.goodsstorage.dto.order.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateOrderedProductDtoRes {

    private final UUID productId;

    private final Long count;
}
