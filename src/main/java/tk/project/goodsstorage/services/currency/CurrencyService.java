package tk.project.goodsstorage.services.currency;

import tk.project.goodsstorage.dto.product.ProductDto;
import tk.project.goodsstorage.enums.Currency;

public interface CurrencyService {
    ProductDto changeCurrency(final ProductDto productDto, final Currency currency);
}
