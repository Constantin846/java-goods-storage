package tk.project.goodsstorage.orchestrator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import tk.project.exceptionhandler.goodsstorage.exceptions.order.OrderNotAccessException;

@Slf4j
@Component
@RequestScope
@Getter
@Setter
public class OrchestratorIdWrapper {

    @Value("${orchestrator-goods-storage.orchestrator-id}")
    private String expectedOrchestratorId;

    private String orchestratorId;

    public void checkOrchestratorAccess() {
        if (!expectedOrchestratorId.equals(orchestratorId)) {
            String msg = String.format("Invalid orchestrator id: %s", orchestratorId);
            log.warn(msg);
            throw new OrderNotAccessException(msg);
        }
    }
}
