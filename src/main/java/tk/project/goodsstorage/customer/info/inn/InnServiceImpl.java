package tk.project.goodsstorage.customer.info.inn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import tk.project.exceptionhandler.goodsstorage.exceptions.customer.RequestFindInnException;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@ConditionalOnMissingBean(InnServiceMock.class)
public class InnServiceImpl implements InnService {
    private static final int RETRY_COUNT = 2;
    private final String uriPostInns;
    public final long timeout;
    private final WebClient webClient;

    public InnServiceImpl(
            @Value("${crm-service.method.get-inns}")
            final String uriPostInns,
            @Value("${crm-service.timeout}")
            final long timeout,
            @Autowired
            @Qualifier("innWebClient")
            final WebClient webClient
    ) {
        this.uriPostInns = uriPostInns;
        this.timeout = timeout;
        this.webClient = webClient;
    }

    private static final ParameterizedTypeReference<Map<String, String>> MAP_TYPE_REF = new ParameterizedTypeReference<>() {};

    @Async
    @Override
    public CompletableFuture<Map<String, String>> sendRequestInnByLogins(final List<String> logins) {
        log.info("Send request to get inns form INN SERVICE");

        return webClient.post()
                .uri(uriPostInns)
                .bodyValue(logins)
                .retrieve()
                .bodyToMono(MAP_TYPE_REF)
                .retryWhen(Retry.fixedDelay(RETRY_COUNT, Duration.ofMillis(timeout)))
                .doOnError(error -> {
                    String message = String.format(
                            "Something went wrong while executing request of inns: %s", error.getMessage());
                    log.warn(message);
                    throw new RequestFindInnException(message, error);
                })
                .toFuture();
    }
}
