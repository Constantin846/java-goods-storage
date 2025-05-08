package tk.project.goodsstorage.services.currency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.dto.CurrenciesDto;
import tk.project.goodsstorage.dto.product.ProductDto;
import tk.project.goodsstorage.enums.Currency;
import tk.project.goodsstorage.services.currency.provider.CurrenciesProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrenciesProvider currenciesProvider;

    @Override
    public ProductDto changeCurrency(final ProductDto productDto, final Currency currency) {
        if (currency == productDto.getCurrency()) {
            return productDto;
        }

        final CurrenciesDto currencies = currenciesProvider.getCurrencies();
        final BigDecimal oldCurrencyRate = getCurrencyRate(currencies, productDto.getCurrency());
        final BigDecimal newCurrencyRate = getCurrencyRate(currencies, currency);

        final BigDecimal newPrice = productDto.getPrice()
                .multiply(oldCurrencyRate)
                .divide(newCurrencyRate, RoundingMode.HALF_EVEN);

        return ProductDto.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .article(productDto.getArticle())
                .description(productDto.getDescription())
                .category(productDto.getCategory())
                .price(newPrice)
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
