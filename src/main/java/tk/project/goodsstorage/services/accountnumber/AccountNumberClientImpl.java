package tk.project.goodsstorage.services.accountnumber;

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
import tk.project.exceptionhandler.goodsstorage.exceptions.customer.RequestFindAccountNumberException;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@ConditionalOnMissingBean(AccountNumberClientMock.class)
public class AccountNumberClientImpl implements AccountNumberClient {
    private static final int RETRY_COUNT = 2;
    private final String uriPostAccountNumbers;
    public final long timeout;
    private final WebClient webClient;

    public AccountNumberClientImpl(
            @Value("${account-service.method.get-account-numbers}")
            final String uriPostAccountNumbers,
            @Value("${account-service.timeout}")
            final long timeout,
            @Autowired
            @Qualifier("accountNumberWebClient")
            final WebClient webClient
    ) {
        this.uriPostAccountNumbers = uriPostAccountNumbers;
        this.timeout = timeout;
        this.webClient = webClient;
    }

    private static final ParameterizedTypeReference<Map<String, String>> MAP_TYPE_REF = new ParameterizedTypeReference<>() {};

    @Async
    @Override
    public CompletableFuture<Map<String, String>> sendRequestAccountNumbersByLogins(final List<String> logins) {
        log.info("Send request to get account numbers form ACCOUNT NUMBER SERVICE");

        return webClient.post()
                .uri(uriPostAccountNumbers)
                .bodyValue(logins)
                .retrieve()
                .bodyToMono(MAP_TYPE_REF)
                .retryWhen(Retry.fixedDelay(RETRY_COUNT, Duration.ofMillis(timeout)))
                .doOnError(error -> {
                    String message = String.format(
                            "Something went wrong while executing request of account numbers: %s", error.getMessage());
                    log.warn(message);
                    throw new RequestFindAccountNumberException(message, error);
                })
                .toFuture();
    }
}
