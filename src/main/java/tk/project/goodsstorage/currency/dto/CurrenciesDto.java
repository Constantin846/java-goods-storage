package tk.project.goodsstorage.currency.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrenciesDto {

    BigDecimal CNY;

    BigDecimal EUR;

    BigDecimal USD;

    public static CurrenciesDto ofDoubles(Double CNY, Double EUR, Double USD) {
        CurrenciesDto currenciesDto = new CurrenciesDto();
        currenciesDto.setCNY(BigDecimal.valueOf(CNY));
        currenciesDto.setEUR(BigDecimal.valueOf(EUR));
        currenciesDto.setUSD(BigDecimal.valueOf(USD));
        return currenciesDto;
    }
}
