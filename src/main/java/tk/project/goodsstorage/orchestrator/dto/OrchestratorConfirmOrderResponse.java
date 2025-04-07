package tk.project.goodsstorage.orchestrator.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrchestratorConfirmOrderResponse {
    UUID businessKey;
}
