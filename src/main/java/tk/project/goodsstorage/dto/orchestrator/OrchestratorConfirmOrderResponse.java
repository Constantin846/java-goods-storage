package tk.project.goodsstorage.dto.orchestrator;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class OrchestratorConfirmOrderResponse {

    private final UUID businessKey;
}
