package tk.project.goodsstorage.dto.orchestrator;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class ConfirmOrderDto {

    private final UUID id;

    private final String deliveryAddress;

    private final BigDecimal price;

    private final String customerLogin;
}
