package tk.project.goodsstorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    public CurrenciesDto(
            @JsonProperty("CNY") final Double cny,
            @JsonProperty("EUR") final Double eur,
            @JsonProperty("USD") final Double usd
    ) {
        this.cny = BigDecimal.valueOf(cny);
        this.eur = BigDecimal.valueOf(eur);
        this.usd = BigDecimal.valueOf(usd);
    }

    public CurrenciesDto(final BigDecimal cny, final BigDecimal eur, final BigDecimal usd) {
        this.cny = cny;
        this.eur = eur;
        this.usd = usd;
    }
}
