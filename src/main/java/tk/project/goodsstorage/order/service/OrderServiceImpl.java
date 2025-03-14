package tk.project.goodsstorage.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.customer.Customer;
import tk.project.goodsstorage.customer.repository.CustomerRepository;
import tk.project.goodsstorage.exceptions.customer.CustomerNotFoundException;
import tk.project.goodsstorage.exceptions.order.OrderNotAccessException;
import tk.project.goodsstorage.exceptions.order.OrderNotFoundException;
import tk.project.goodsstorage.exceptions.order.OrderStatusAlreadyCancelledException;
import tk.project.goodsstorage.exceptions.order.OrderStatusAlreadyRejectedException;
import tk.project.goodsstorage.exceptions.order.OrderStatusNotCreateException;
import tk.project.goodsstorage.exceptions.product.ProductCountNotEnoughException;
import tk.project.goodsstorage.exceptions.product.ProductNotAvailableException;
import tk.project.goodsstorage.exceptions.product.ProductsNotFoundByIdsException;
import tk.project.goodsstorage.order.dto.SaveOrderedProductDto;
import tk.project.goodsstorage.order.dto.create.CreateOrderDto;
import tk.project.goodsstorage.order.dto.find.FindOrderDto;
import tk.project.goodsstorage.order.dto.find.FindOrderedProductDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDtoRes;
import tk.project.goodsstorage.order.dto.update.UpdateOrderStatusDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderedProductDto;
import tk.project.goodsstorage.order.mapper.OrderDtoMapper;
import tk.project.goodsstorage.order.model.Order;
import tk.project.goodsstorage.order.model.OrderStatus;
import tk.project.goodsstorage.order.model.OrderedProduct;
import tk.project.goodsstorage.order.repository.OrderRepository;
import tk.project.goodsstorage.order.repository.OrderedProductRepository;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CustomerRepository customerRepository;
    private final OrderDtoMapper mapper;
    private final OrderedProductRepository orderedProductRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public UUID create(CreateOrderDto orderDto) {
        Customer customer = getCustomerById(orderDto.getCustomerId());
        Order order = mapper.toOrder(orderDto);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);
        order.setProducts(new HashSet<>());

        addOrderedProducts(orderDto.getProducts(), order);
        order = orderRepository.save(order);
        return order.getId();
    }

    @Transactional
    @Override
    public void confirmById(UUID orderId, long customerId) {
        // todo
    }

    @Override
    public FindOrderDto findById(UUID orderId, long customerId) {
        Order order = getOrderByIdFetch(orderId);
        checkCustomerAccessToOrder(customerId, order);

        FindOrderDto findOrderDto = new FindOrderDto();
        findOrderDto.setOrderId(order.getId());

        List<FindOrderedProductDto> orderedProductDto = orderedProductRepository.findAllWithNameByOrderId(order.getId());
        findOrderDto.setProducts(orderedProductDto);

        BigDecimal totalPrice = orderedProductDto.stream()
                .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        findOrderDto.setTotalPrice(totalPrice);
        return findOrderDto;
    }

    @Transactional
    @Override
    public UpdateOrderDtoRes update(UpdateOrderDto updateOrderDto) {
        Order oldOrder = getOrderByIdFetch(updateOrderDto.getId());
        checkCustomerAccessToOrder(updateOrderDto.getCustomerId(), oldOrder);
        checkOrderStatusIsCreate(oldOrder);

        if (Objects.nonNull(updateOrderDto.getDeliveryAddress())) {
            oldOrder.setDeliveryAddress(updateOrderDto.getDeliveryAddress());
        }

        Set<UpdateOrderedProductDto> updateOrderedProductsDto = updateOrderDto.getProducts();
        if (Objects.nonNull(updateOrderedProductsDto) && !updateOrderedProductsDto.isEmpty()) {
            addOrderedProducts(updateOrderedProductsDto, oldOrder);
        }

        orderRepository.save(oldOrder);
        return mapper.toUpdateOrderDtoRes(oldOrder);
    }

    @Transactional
    @Override
    public UpdateOrderStatusDto setStatusDone(UUID orderId, long customerId) {
        Order order = getOrderByIdFetch(orderId);
        checkCustomerAccessToOrder(customerId, order);
        checkOrderStatusIsNotCancelled(order);
        checkOrderStatusIsNotRejected(order);
        order.setStatus(OrderStatus.DONE);
        orderRepository.save(order);
        return new UpdateOrderStatusDto(order.getStatus());
    }

    @Transactional
    @Override
    public void deleteById(UUID orderId, long customerId) {
        Order order = getOrderByIdFetch(orderId);
        checkCustomerAccessToOrder(customerId, order);
        checkOrderStatusIsCreate(order);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        cancelOrderedProducts(order);
    }

    private void addOrderedProducts(Set<? extends SaveOrderedProductDto> orderedProductsDto, Order order) {
        Map<UUID, Product> productMap = getProductsByIdsForUpdate(orderedProductsDto);
        Map<UUID, OrderedProduct> orderedProductMap = order.getProducts().stream()
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
    }

    private OrderedProduct createOrderedProduct(SaveOrderedProductDto orderedProductDto, Product product, Order order) {
        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setOrder(order);
        orderedProduct.setProductId(product.getId());
        orderedProduct.setPrice(product.getPrice());
        orderedProduct.setCount(orderedProductDto.getCount());
        return orderedProduct;
    }

    private OrderedProduct updateOrderedProduct(SaveOrderedProductDto orderedProductDto, Product product, OrderedProduct orderedProduct) {
        orderedProduct.setPrice(product.getPrice());
        orderedProduct.setCount(orderedProduct.getCount() + orderedProductDto.getCount());
        return orderedProduct;
    }

    private void decreaseProductCount(Product product, long subtractCount) {
        if (subtractCount > product.getCount()) {
            String message = String.format("There is not enough product count in stock. " +
                    "Count = %s of product with id = %s", product.getCount(), product.getId());
            log.warn(message);
            throw new ProductCountNotEnoughException(message);
        }
        product.setCount(product.getCount() - subtractCount);
    }

    private Map<UUID, Product> getProductsByIdsForUpdate(Set<? extends SaveOrderedProductDto> orderedProductsDto) {
        Set<UUID> productIds = orderedProductsDto.stream()
                .map(SaveOrderedProductDto::getId)
                .collect(Collectors.toSet());

        Map<UUID, Product> productMap = productRepository.findMapByIdsForUpdate(productIds);

        productIds.removeAll(productMap.keySet());
        if (!productIds.isEmpty()) {
            String message = String.format("Products were not found by ids: %s", productIds);
            log.warn(message);
            throw new ProductsNotFoundByIdsException(message, productIds);
        }
        return productMap;
    }

    private Order getOrderByIdFetch(UUID orderId) {
        return orderRepository.findByIdFetch(orderId).orElseThrow(() -> throwOrderNotFoundException(orderId));
    }

    private OrderNotFoundException throwOrderNotFoundException(UUID orderId) {
        String message = String.format("Order was not found by id: %s", orderId);
        log.warn(message);
        return new OrderNotFoundException(message);
    }

    private Customer getCustomerById(long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> {
            String message = String.format("Customer was not found by id: %s", customerId);
            log.warn(message);
            return new CustomerNotFoundException(message);
        });
    }

    private void checkProductIsAvailable(Product product) {
        if (!product.getIsAvailable()) {
            String message = String.format("Product with id = %s is not available", product.getId());
            log.warn(message);
            throw new ProductNotAvailableException(message);
        }
    }

    private void checkOrderStatusIsCreate(Order order) {
        if (order.getStatus() != OrderStatus.CREATED) {
            String message = String.format("Order status must be CREATE but now status: %s", order.getStatus());
            log.warn(message);
            throw new OrderStatusNotCreateException(message);
        }
    }

    private void checkOrderStatusIsNotCancelled(Order order) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            String message = String.format("Order status has already been %s", OrderStatus.CANCELLED);
            log.warn(message);
            throw new OrderStatusAlreadyCancelledException(message);
        }
    }

    private void checkOrderStatusIsNotRejected(Order order) {
        if (order.getStatus() == OrderStatus.REJECTED) {
            String message = String.format("Order status has already been %s", OrderStatus.REJECTED);
            log.warn(message);
            throw new OrderStatusAlreadyRejectedException(message);
        }
    }

    private void checkCustomerAccessToOrder(Long customerId, Order order) {
        if (!order.getCustomer().getId().equals(customerId)) {
            String message = "No access to order";
            log.warn(message);
            throw new OrderNotAccessException(message);
        }
    }

    private void cancelOrderedProducts(Order order) {
        Set<OrderedProduct> orderedProducts = order.getProducts();
        Map<UUID, OrderedProduct>  orderedProductMap = orderedProducts.stream()
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
}
