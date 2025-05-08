package tk.project.goodsstorage.order.dto.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UpdateOrderedProductResponse {

    private final UUID productId;

    private final Long count;
}
