package tk.project.goodsstorage.services.currency.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import tk.project.exceptionhandler.goodsstorage.exceptions.currency.RequestGetCurrenciesException;
import tk.project.goodsstorage.dto.CurrenciesDto;

import java.math.BigDecimal;
import java.util.Random;

@Slf4j
@Service
@ConditionalOnExpression("'${app.currency-client.type}'.equals('mock')")
public class CurrencyClientMock implements CurrencyClient {
    private static final Double maxCNY = 30.0;
    private static final Double maxEUR = 150.0;
    private static final Double maxUSD = 140.0;
    private static final Integer ONE = 1;
    private static final Integer TWO = 2;

    private final Random random;

    public CurrencyClientMock() {
        random = new Random();
    }

    @Override
    public CurrenciesDto sendRequestGetCurrencies() {
        log.info("Get currency form CURRENCY SERVICE MOCK");

        if (random.nextInt(TWO) == ONE) {
            throw new RequestGetCurrenciesException("Currency service mock threw exception");
        }
        return CurrenciesDto.builder()
                .cny(generateBigDecimal(maxCNY))
                .eur(generateBigDecimal(maxEUR))
                .usd(generateBigDecimal(maxUSD))
                .build();
    }

    private BigDecimal generateBigDecimal(final double max) {
        return BigDecimal.valueOf(random.nextDouble(max));
    }
}
