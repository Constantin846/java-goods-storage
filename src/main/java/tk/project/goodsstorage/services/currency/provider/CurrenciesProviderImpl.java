package tk.project.goodsstorage.services.currency.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.dto.CurrenciesDto;
import tk.project.goodsstorage.exceptionhandler.exceptions.currency.RequestGetCurrenciesException;
import tk.project.goodsstorage.interaction.client.CurrencyClient;
import tk.project.goodsstorage.services.currency.loader.CurrencyFileLoader;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrenciesProviderImpl implements CurrenciesProvider {
    private final CurrencyFileLoader currencyFileLoader;
    private final CurrencyClient currencyClient;

    @Cacheable(value = "currencies", unless = "#result == null")
    @Override
    public CurrenciesDto getCurrencies() {
        CurrenciesDto currenciesDto;

        try {
            currenciesDto = currencyClient.sendRequestGetCurrencies();
            log.info("Currencies was gotten with response from currency service");

        } catch (RequestGetCurrenciesException e) {
            currenciesDto = currencyFileLoader.loadCurrencies();
            log.info("Currencies was loaded from currency file");
        }
        return currenciesDto;
    }

    @CacheEvict(value = "currencies", allEntries = true)
    @Scheduled(fixedRateString = "60000")
    public void emptyCurrenciesCache() {
        log.info("Emptying currencies cache");
    }
}
