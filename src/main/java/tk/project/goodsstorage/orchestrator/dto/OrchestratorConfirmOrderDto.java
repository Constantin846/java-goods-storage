package tk.project.goodsstorage.orchestrator.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrchestratorConfirmOrderDto {

    UUID id;

    String deliveryAddress;

    String customerInn;

    String customerAccountNumber;

    BigDecimal price;

    String customerLogin;
}
