package tk.project.goodsstorage.product.currency;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

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
