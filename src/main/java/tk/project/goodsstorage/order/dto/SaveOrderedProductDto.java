package tk.project.goodsstorage.order.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class SaveOrderedProductDto {

    private final UUID id;

    private final Long count;
}
