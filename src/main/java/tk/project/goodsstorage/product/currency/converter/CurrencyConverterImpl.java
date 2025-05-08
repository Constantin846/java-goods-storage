package tk.project.goodsstorage.product.currency.converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.product.currency.CurrenciesDto;
import tk.project.goodsstorage.product.currency.Currency;
import tk.project.goodsstorage.product.currency.provider.CurrenciesProvider;
import tk.project.goodsstorage.product.dto.ProductDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyConverterImpl implements CurrencyConverter {
    private final CurrenciesProvider currenciesProvider;

    @Override
    public ProductDto changeCurrency(final ProductDto productDto, final Currency currency) {
        if (currency == Currency.RUS) {
            return productDto;
        }

        final CurrenciesDto currencies = currenciesProvider.getCurrencies();
        final BigDecimal currencyRate = getCurrencyRate(currencies, currency);
        final BigDecimal currentPrice = productDto.getPrice().divide(currencyRate, RoundingMode.HALF_EVEN);

        return ProductDto.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .article(productDto.getArticle())
                .description(productDto.getDescription())
                .category(productDto.getCategory())
                .price(currentPrice)
                .count(productDto.getCount())
                .lastCountUpdateTime(productDto.getLastCountUpdateTime())
                .createDate(productDto.getCreateDate())
                .isAvailable(productDto.getIsAvailable())
                .currency(currency)
                .build();
    }

    private BigDecimal getCurrencyRate(final CurrenciesDto currencies, final Currency currency) {
        return switch (currency) {
            case CNY -> currencies.getCny();
            case EUR -> currencies.getEur();
            case RUS -> BigDecimal.ONE;
            case USD -> currencies.getUsd();
        };
    }
}
