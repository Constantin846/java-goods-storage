package tk.project.goodsstorage.product.currency.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import tk.project.goodsstorage.exceptions.currency.RequestGetCurrenciesException;
import tk.project.goodsstorage.product.currency.CurrenciesDto;

import java.time.Duration;

@Slf4j
@Service
@ConditionalOnExpression("'${app.currency-service.type}'.equals('real')")
public class CurrencyServiceImpl implements CurrencyService {
    private static final int RETRY_COUNT = 2;
    private final String uriGetCurrencies;
    private final long timeout;
    private final WebClient webClient;

    public CurrencyServiceImpl(
            @Value("${currency-service.method.get-currencies}")
            String uriGetCurrencies,
            @Value("${currency-service.timeout}")
            long timeout,
            @Autowired
            @Qualifier("currencyWebClient")
            WebClient webClient
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
