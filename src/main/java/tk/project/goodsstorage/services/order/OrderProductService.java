package tk.project.goodsstorage.services.order;

import tk.project.goodsstorage.dto.order.SaveOrderedProductDto;
import tk.project.goodsstorage.models.order.Order;

import java.util.Set;

public interface OrderProductService {
    void cancelOrderedProducts(final Order order);

    Order addOrderedProducts(final Set<? extends SaveOrderedProductDto> orderedProductsDto, Order order);
}
