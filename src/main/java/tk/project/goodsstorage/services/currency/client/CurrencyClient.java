package tk.project.goodsstorage.services.currency.client;

import tk.project.goodsstorage.dto.CurrenciesDto;

public interface CurrencyClient {
    CurrenciesDto sendRequestGetCurrencies();
}
