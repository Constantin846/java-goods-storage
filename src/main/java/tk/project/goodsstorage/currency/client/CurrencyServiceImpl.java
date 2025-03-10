package tk.project.goodsstorage.currency.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import tk.project.goodsstorage.currency.CurrenciesDto;
import tk.project.goodsstorage.exceptions.currency.RequestGetCurrenciesException;

import java.time.Duration;

@Slf4j
@Service
@ConditionalOnExpression("'${app.currency-service.type}'.equals('real')")
public class CurrencyServiceImpl implements CurrencyService {
    private final String uriGetCurrencies;
    public final long timeout;
    private final WebClient webClient;

    public CurrencyServiceImpl(
            @Value("${currency-service.method.get-currencies}")
            String uriGetCurrencies,
            @Value("${currency-service.timeout}")
            long timeout,
            @Autowired
            WebClient webClient
    ) {
        this.uriGetCurrencies = uriGetCurrencies;
        this.timeout = timeout;
        this.webClient = webClient;
    }

    @Override
    public CurrenciesDto sendRequestGetCurrencies() {
        log.info("Send request get currencies form CURRENCY SERVICE REAL");

        return webClient.get()
                .uri(uriGetCurrencies)
                .retrieve()
                .bodyToMono(CurrenciesDto.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofMillis(timeout)))
                .doOnError(error -> {
                    String message = "Something went wrong while executing request of currencies";
                    log.warn(message);
                    throw new RequestGetCurrenciesException(message, error);
                })
                .block();
    }
}
