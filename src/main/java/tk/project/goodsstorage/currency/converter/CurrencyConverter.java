package tk.project.goodsstorage.currency.converter;

import tk.project.goodsstorage.currency.Currency;
import tk.project.goodsstorage.product.dto.ProductDto;

import java.util.List;

public interface CurrencyConverter {
    ProductDto changeCurrency(ProductDto productDto, Currency currency);

    List<ProductDto> changeCurrency(List<ProductDto> productsDto, Currency currency);
}
