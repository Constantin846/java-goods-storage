package tk.project.goodsstorage.interaction.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import tk.project.goodsstorage.dto.CurrenciesDto;
import tk.project.goodsstorage.exceptionhandler.exceptions.currency.RequestGetCurrenciesException;

import java.time.Duration;

@Slf4j
@Service
@ConditionalOnMissingBean(CurrencyClientMock.class)
public class CurrencyClientImpl implements CurrencyClient {
    private static final int RETRY_COUNT = 2;
    private final String uriGetCurrencies;
    private final long timeout;
    private final WebClient webClient;

    public CurrencyClientImpl(
            @Value("${currency-service.method.get-currencies}")
            final String uriGetCurrencies,
            @Value("${currency-service.timeout}")
            final long timeout,
            @Autowired
            @Qualifier("currencyWebClient")
            final WebClient webClient
    ) {
        this.uriGetCurrencies = uriGetCurrencies;
        this.timeout = timeout;
        this.webClient = webClient;
    }

    @Override
    public CurrenciesDto sendRequestGetCurrencies() {
        log.info("Send request to get currencies form CURRENCY SERVICE REAL");

        return webClient.get()
                .uri(uriGetCurrencies)
                .retrieve()
                .bodyToMono(CurrenciesDto.class)
                .retryWhen(Retry.fixedDelay(RETRY_COUNT, Duration.ofMillis(timeout)))
                .doOnError(error -> {
                    String message = "Something went wrong while executing request of currencies";
                    log.warn(message);
                    throw new RequestGetCurrenciesException(message, error);
                })
                .block();
    }
}
