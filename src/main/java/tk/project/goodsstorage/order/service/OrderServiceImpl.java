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
import tk.project.goodsstorage.order.dto.OrderProductDto;
import tk.project.goodsstorage.order.dto.SaveOrderProductDto;
import tk.project.goodsstorage.order.dto.create.CreateOrderDto;
import tk.project.goodsstorage.order.dto.find.FindOrderDto;
import tk.project.goodsstorage.order.dto.find.FindOrderProductDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDtoRes;
import tk.project.goodsstorage.order.dto.update.UpdateOrderProductDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderStatusDto;
import tk.project.goodsstorage.order.mapper.OrderDtoMapper;
import tk.project.goodsstorage.order.model.Order;
import tk.project.goodsstorage.order.model.OrderProduct;
import tk.project.goodsstorage.order.model.Status;
import tk.project.goodsstorage.order.repository.OrderProductRepository;
import tk.project.goodsstorage.order.repository.OrderRepository;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CustomerRepository customerRepository;
    private final OrderDtoMapper mapper;
    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public UUID create(CreateOrderDto orderDto) {
        Customer customer = getCustomerById(orderDto.getCustomerId());
        Order order = mapper.toOrder(orderDto);
        order.setCustomer(customer);
        order.setStatus(Status.CREATED);
        order = orderRepository.save(order);

        saveOrderProducts(orderDto.getProducts(), order);
        return order.getId();
    }

    @Transactional
    @Override
    public void confirmById(UUID orderId, long customerId) {
        // todo
    }

    @Override
    public FindOrderDto findById(UUID orderId, long customerId) {
        Order order = getOrderById(orderId);
        checkCustomerAccessToOrder(customerId, order);

        FindOrderDto findOrderDto = new FindOrderDto();
        findOrderDto.setOrderId(order.getId());

        List<FindOrderProductDto> orderProductDto = orderProductRepository.findAllWithNameByOrderId(order.getId());
        findOrderDto.setProducts(orderProductDto);

        BigDecimal totalPrice = orderProductDto.stream()
                .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        findOrderDto.setTotalPrice(totalPrice);
        return findOrderDto;
    }

    @Transactional
    @Override
    public UpdateOrderDtoRes update(UpdateOrderDto updateOrderDto) {
        Order oldOrder = getOrderByIdForUpdate(updateOrderDto.getId());
        checkOrderStatusIsCreate(oldOrder);
        checkCustomerAccessToOrder(updateOrderDto.getCustomerId(), oldOrder);

        if (Objects.nonNull(updateOrderDto.getDeliveryAddress())) {
            oldOrder.setDeliveryAddress(updateOrderDto.getDeliveryAddress());
        }
        orderRepository.save(oldOrder);
        UpdateOrderDtoRes orderDtoResponse = mapper.toUpdateOrderDtoRes(oldOrder);

        Set<UpdateOrderProductDto> updateOrderProductsDto = updateOrderDto.getProducts();
        if (Objects.nonNull(updateOrderProductsDto) && !updateOrderProductsDto.isEmpty()) {
            saveOrderProducts(updateOrderProductsDto, oldOrder);
            orderDtoResponse.setProducts(orderProductRepository.findAllWithoutPriceByOrderId(oldOrder.getId()));
        }
        return orderDtoResponse;
    }

    @Transactional
    @Override
    public UpdateOrderStatusDto setStatusDone(UUID orderId, long customerId) {
        Order order = getOrderByIdForUpdate(orderId);
        checkCustomerAccessToOrder(customerId, order);
        checkOrderStatusIsNotCancelled(order);
        checkOrderStatusIsNotRejected(order);
        order.setStatus(Status.DONE);
        orderRepository.save(order);
        return new UpdateOrderStatusDto(order.getStatus());
    }

    @Transactional
    @Override
    public void deleteById(UUID orderId, long customerId) {
        Order order = getOrderByIdForUpdate(orderId);
        checkCustomerAccessToOrder(customerId, order);
        checkOrderStatusIsCreate(order);
        order.setStatus(Status.CANCELLED);
        orderRepository.save(order);

        cancelOrderProducts(order.getId());
    }

    private void saveOrderProducts(Set<? extends SaveOrderProductDto> orderProductsDto, Order order) {
        Map<UUID, Product> productMap = getProductsByIdsForUpdate(orderProductsDto);
        Map<UUID, OrderProduct> orderProductMap =
                orderProductRepository.findProductIdOrderProductMapByOrderIdForUpdate(order.getId());

        List<OrderProduct> orderProducts = orderProductsDto.stream()
                .map(orderProductDto -> {
                    Product product = productMap.get(orderProductDto.getId());
                    checkProductIsAvailable(product);
                    decreaseProductCount(product, orderProductDto.getCount());

                    if (orderProductMap.containsKey(orderProductDto.getId())) {
                        return this.updateOrderProduct(
                                orderProductDto, product, orderProductMap.get(orderProductDto.getId()));
                    } else {
                        return this.createOrderProduct(orderProductDto, product, order);
                    }
                }).toList();

        productRepository.saveAll(productMap.values());
        orderProductRepository.saveAll(orderProducts);
    }

    private OrderProduct createOrderProduct(SaveOrderProductDto orderProductDto, Product product, Order order) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setProductId(product.getId());
        orderProduct.setPrice(product.getPrice());
        orderProduct.setCount(orderProductDto.getCount());
        return orderProduct;
    }

    private OrderProduct updateOrderProduct(SaveOrderProductDto orderProductDto, Product product, OrderProduct orderProduct) {
        orderProduct.setPrice(product.getPrice());
        orderProduct.setCount(orderProduct.getCount() + orderProductDto.getCount());
        return orderProduct;
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

    private Map<UUID, Product> getProductsByIdsForUpdate(Set<? extends SaveOrderProductDto> orderProductsDto) {
        Set<UUID> productIds = orderProductsDto.stream()
                .map(SaveOrderProductDto::getId)
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

    private Order getOrderByIdForUpdate(UUID orderId) {
        return orderRepository.findByIdLocked(orderId).orElseThrow(() -> throwOrderNotFoundException(orderId));
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> throwOrderNotFoundException(orderId));
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
        if (order.getStatus() != Status.CREATED) {
            String message = String.format("Order status must be CREATE but now status: %s", order.getStatus());
            log.warn(message);
            throw new OrderStatusNotCreateException(message);
        }
    }

    private void checkOrderStatusIsNotCancelled(Order order) {
        if (order.getStatus() == Status.CANCELLED) {
            String message = String.format("Order status has already been %s", Status.CANCELLED);
            log.warn(message);
            throw new OrderStatusAlreadyCancelledException(message);
        }
    }

    private void checkOrderStatusIsNotRejected(Order order) {
        if (order.getStatus() == Status.REJECTED) {
            String message = String.format("Order status has already been %s", Status.REJECTED);
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

    private void cancelOrderProducts(UUID orderId) {
        List<? extends OrderProductDto> orderProductsDto = orderProductRepository.findAllWithoutPriceByOrderId(orderId);
        Map<UUID, OrderProductDto>  orderProductDtoMap = orderProductsDto.stream()
                .collect(Collectors.toMap(OrderProductDto::getId, orderProductDto -> orderProductDto));

        Set<Product> products = productRepository.findAllByIdsForUpdate(orderProductDtoMap.keySet());

        products = products.stream()
                .map(product -> {
                    OrderProductDto orderProductDto = orderProductDtoMap.get(product.getId());
                    product.setCount(product.getCount() + orderProductDto.getCount());
                    return product;
                }).collect(Collectors.toSet());
        productRepository.saveAll(products);
    }
}
