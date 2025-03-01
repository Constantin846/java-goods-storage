package tk.project.goodsstorage.currency.loader;

import tk.project.goodsstorage.currency.dto.CurrenciesDto;

public interface CurrencyFileLoader {
    CurrenciesDto loadCurrencies();
}
