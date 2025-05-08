package tk.project.goodsstorage.services.orchestrator;

import tk.project.goodsstorage.dto.orchestrator.OrchestratorConfirmOrderDto;

import java.util.UUID;

public interface OrchestratorClient {
    UUID sendRequestConfirmOrder(final OrchestratorConfirmOrderDto orderDto);
}
