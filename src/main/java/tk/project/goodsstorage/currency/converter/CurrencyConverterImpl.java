package tk.project.goodsstorage.currency.converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.currency.CurrenciesDto;
import tk.project.goodsstorage.currency.Currency;
import tk.project.goodsstorage.currency.provider.CurrenciesProvider;
import tk.project.goodsstorage.product.dto.ProductDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyConverterImpl implements CurrencyConverter {
    private final CurrenciesProvider currenciesProvider;

    @Override
    public ProductDto changeCurrency(ProductDto productDto, Currency currency) {
        if (currency == Currency.RUS) {
            productDto.setCurrency(Currency.RUS);
            return productDto;
        }

        CurrenciesDto currencies = currenciesProvider.getCurrencies();
        BigDecimal currencyRate = getCurrencyRate(currencies, currency);

        productDto.setPrice(productDto.getPrice().divide(currencyRate, RoundingMode.HALF_EVEN));
        productDto.setCurrency(currency);

        log.info("ProductDto id={} was set currency {}", productDto.getId(), productDto.getCurrency());
        return productDto;
    }

    @Override
    public List<ProductDto> changeCurrency(List<ProductDto> productsDto, Currency currency) {
        return productsDto.stream()
                .map(productDto -> this.changeCurrency(productDto, currency))
                .toList();
    }

    private BigDecimal getCurrencyRate(CurrenciesDto currencies, Currency currency) {
        return switch (currency) {
            case CNY -> currencies.getCny();
            case EUR -> currencies.getEur();
            case RUS -> BigDecimal.ONE;
            case USD -> currencies.getUsd();
        };
    }
}
