package tk.project.goodsstorage.services.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.ProductCountNotEnoughException;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.ProductNotAvailableException;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.ProductsNotFoundByIdsException;
import tk.project.goodsstorage.dto.order.SaveOrderedProductDto;
import tk.project.goodsstorage.models.order.Order;
import tk.project.goodsstorage.models.order.OrderedProduct;
import tk.project.goodsstorage.models.product.Product;
import tk.project.goodsstorage.repositories.product.ProductRepository;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProductServiceImpl implements OrderProductService {
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public void cancelOrderedProducts(final Order order) {
        final Set<OrderedProduct> orderedProducts = order.getProducts();
        final Map<UUID, OrderedProduct>  orderedProductMap = orderedProducts.stream()
                .collect(Collectors.toMap(OrderedProduct::getProductId, Function.identity()));

        Set<Product> products = productRepository.findAllByIdsForUpdate(orderedProductMap.keySet());

        products = products.stream()
                .map(product -> {
                    OrderedProduct orderedProduct = orderedProductMap.get(product.getId());
                    product.setCount(product.getCount() + orderedProduct.getCount());
                    return product;
                }).collect(Collectors.toSet());
        productRepository.saveAll(products);
    }

    @Transactional
    @Override
    public Order addOrderedProducts(final Set<? extends SaveOrderedProductDto> orderedProductsDto, Order order) {
        final Map<UUID, Product> productMap = getProductsByIdsForUpdate(orderedProductsDto);
        final Map<UUID, OrderedProduct> orderedProductMap = order.getProducts().stream()
                .collect(Collectors.toMap(OrderedProduct::getProductId, Function.identity()));

        Set<OrderedProduct> orderedProducts = orderedProductsDto.stream()
                .map(orderedProductDto -> {
                    Product product = productMap.get(orderedProductDto.getId());
                    checkProductIsAvailable(product);
                    decreaseProductCount(product, orderedProductDto.getCount());

                    if (orderedProductMap.containsKey(orderedProductDto.getId())) {
                        return this.updateOrderedProduct(
                                orderedProductDto, product, orderedProductMap.get(orderedProductDto.getId()));
                    } else {
                        return this.createOrderedProduct(orderedProductDto, product, order);
                    }
                }).collect(Collectors.toSet());

        order.getProducts().addAll(orderedProducts);
        return order;
    }

    private OrderedProduct createOrderedProduct(final SaveOrderedProductDto orderedProductDto,
                                                final Product product, final Order order) {
        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setOrder(order);
        orderedProduct.setProductId(product.getId());
        orderedProduct.setPrice(product.getPrice());
        orderedProduct.setCount(orderedProductDto.getCount());
        return orderedProduct;
    }

    private OrderedProduct updateOrderedProduct(final SaveOrderedProductDto orderedProductDto,
                                                final Product product, OrderedProduct orderedProduct) {
        orderedProduct.setPrice(product.getPrice());
        orderedProduct.setCount(orderedProduct.getCount() + orderedProductDto.getCount());
        return orderedProduct;
    }

    private void decreaseProductCount(Product product, final long subtractCount) {
        if (subtractCount > product.getCount()) {
            final String message = String.format("There is not enough product count in stock. " +
                    "Count = %s of product with id = %s", product.getCount(), product.getId());
            log.warn(message);
            throw new ProductCountNotEnoughException(message);
        }
        product.setCount(product.getCount() - subtractCount);
    }

    private Map<UUID, Product> getProductsByIdsForUpdate(final Set<? extends SaveOrderedProductDto> orderedProductsDto) {
        Set<UUID> productIds = orderedProductsDto.stream()
                .map(SaveOrderedProductDto::getId)
                .collect(Collectors.toSet());

        final Map<UUID, Product> productMap = productRepository.findMapByIdsForUpdate(productIds);

        productIds.removeAll(productMap.keySet());
        if (!productIds.isEmpty()) {
            final String message = String.format("Products were not found by ids: %s", productIds);
            log.warn(message);
            throw new ProductsNotFoundByIdsException(message, productIds);
        }
        return productMap;
    }

    private void checkProductIsAvailable(final Product product) {
        if (!product.getIsAvailable()) {
            final String message = String.format("Product with id = %s is not available", product.getId());
            log.warn(message);
            throw new ProductNotAvailableException(message);
        }
    }
}
