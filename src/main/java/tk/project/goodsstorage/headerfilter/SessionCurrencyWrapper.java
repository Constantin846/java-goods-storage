package tk.project.goodsstorage.headerfilter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import tk.project.goodsstorage.enums.Currency;

@Component
@SessionScope
@Getter
@Setter
public class SessionCurrencyWrapper {
    private Currency currency;

    public SessionCurrencyWrapper() {
        currency = Currency.RUS;
    }
}
