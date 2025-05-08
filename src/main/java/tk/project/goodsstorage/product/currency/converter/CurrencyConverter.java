package tk.project.goodsstorage.product.currency.converter;

import tk.project.goodsstorage.product.currency.Currency;
import tk.project.goodsstorage.product.dto.ProductDto;

public interface CurrencyConverter {
    ProductDto changeCurrency(final ProductDto productDto, final Currency currency);
}
