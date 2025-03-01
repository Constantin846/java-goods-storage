package tk.project.goodsstorage.currency.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrenciesDtoDoubles {
    @JsonProperty("CNY")
    Double CNY;

    @JsonProperty("EUR")
    Double EUR;

    @JsonProperty("USD")
    Double USD;
}
