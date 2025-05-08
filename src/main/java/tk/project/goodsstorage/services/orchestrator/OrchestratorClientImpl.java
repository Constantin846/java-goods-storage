package tk.project.goodsstorage.services.orchestrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import tk.project.exceptionhandler.goodsstorage.exceptions.order.RequestConfirmOrderToOrchestratorException;
import tk.project.goodsstorage.dto.orchestrator.OrchestratorConfirmOrderDto;
import tk.project.goodsstorage.dto.orchestrator.OrchestratorConfirmOrderResponse;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class OrchestratorClientImpl implements OrchestratorClient {
    private static final int RETRY_COUNT = 2;
    private final String confirmOrder;
    private final long timeout;
    private final WebClient webClient;

    public OrchestratorClientImpl(
            @Value("${orchestrator-goods-storage.method.confirm-order}")
            final String confirmOrder,
            @Value("${orchestrator-goods-storage.timeout}")
            final long timeout,
            @Autowired
            @Qualifier("orchestratorWebClient")
            final WebClient webClient
    ) {
        this.confirmOrder = confirmOrder;
        this.timeout = timeout;
        this.webClient = webClient;
    }

    @Override
    public UUID sendRequestConfirmOrder(OrchestratorConfirmOrderDto orderDto) {
        log.info("Send request to confirm order");

        final OrchestratorConfirmOrderResponse response =
                webClient.post()
                .uri(confirmOrder)
                .bodyValue(orderDto)
                .retrieve()
                .bodyToMono(OrchestratorConfirmOrderResponse.class)
                .retryWhen(Retry.fixedDelay(RETRY_COUNT, Duration.ofMillis(timeout)))
                .doOnError(error -> {
                    String message = "Something went wrong while executing request to confirm order";
                    log.warn(message);
                    throw new RequestConfirmOrderToOrchestratorException(message, error);
                })
                .block();

        return response != null ? response.getBusinessKey() : null;
    }
}
