package tk.project.goodsstorage.product.currency;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrenciesDto {

    BigDecimal cny;

    BigDecimal eur;

    BigDecimal usd;

    public static CurrenciesDto ofDoubles(Double cny, Double eur, Double usd) {
        CurrenciesDto currenciesDto = new CurrenciesDto();
        currenciesDto.setCny(BigDecimal.valueOf(cny));
        currenciesDto.setEur(BigDecimal.valueOf(eur));
        currenciesDto.setUsd(BigDecimal.valueOf(usd));
        return currenciesDto;
    }
}
