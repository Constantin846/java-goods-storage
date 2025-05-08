package tk.project.goodsstorage.product.currency;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CurrenciesDto {

    private final BigDecimal cny;

    private final BigDecimal eur;

    private final BigDecimal usd;

    public static CurrenciesDto ofDoubles(Double cny, Double eur, Double usd) {
        return CurrenciesDto.builder()
                .cny(BigDecimal.valueOf(cny))
                .eur(BigDecimal.valueOf(eur))
                .usd(BigDecimal.valueOf(usd))
                .build();
    }
}
