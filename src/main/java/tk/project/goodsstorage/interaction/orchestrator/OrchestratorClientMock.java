package tk.project.goodsstorage.interaction.orchestrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.dto.orchestrator.OrchestratorConfirmOrderDto;

import java.util.UUID;

@Slf4j
@Service
@ConditionalOnExpression("'${app.orchestrator-client.type}'.equals('mock')")
public class OrchestratorClientMock implements OrchestratorClient {

    @Override
    public UUID sendRequestConfirmOrder(final OrchestratorConfirmOrderDto orderDto) {
        log.info("Generate business key in mock for order with id: {}", orderDto.getId());

        return UUID.randomUUID();
    }
}
