package tk.project.goodsstorage.orchestrator;

import tk.project.goodsstorage.orchestrator.dto.OrchestratorConfirmOrderDto;

import java.util.UUID;

public interface OrchestratorService {
    UUID sendRequestConfirmOrder(OrchestratorConfirmOrderDto orderDto);
}
