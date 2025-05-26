package tk.project.goodsstorage.interaction.orchestrator;

import tk.project.goodsstorage.dto.orchestrator.OrchestratorConfirmOrderDto;

import java.util.UUID;

public interface OrchestratorClient {
    UUID sendRequestConfirmOrder(final OrchestratorConfirmOrderDto orderDto);
}
