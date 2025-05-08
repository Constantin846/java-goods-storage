package tk.project.goodsstorage.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.project.exceptionhandler.goodsstorage.exceptions.customer.CustomerNotFoundException;
import tk.project.exceptionhandler.goodsstorage.exceptions.order.OrderNotAccessException;
import tk.project.exceptionhandler.goodsstorage.exceptions.order.OrderNotFoundException;
import tk.project.exceptionhandler.goodsstorage.exceptions.order.OrderStatusAlreadyCancelledException;
import tk.project.exceptionhandler.goodsstorage.exceptions.order.OrderStatusAlreadyRejectedException;
import tk.project.exceptionhandler.goodsstorage.exceptions.order.OrderStatusNotCreateException;
import tk.project.exceptionhandler.goodsstorage.exceptions.order.OrderStatusNotProcessingException;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.ProductCountNotEnoughException;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.ProductNotAvailableException;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.ProductsNotFoundByIdsException;
import tk.project.goodsstorage.customer.Customer;
import tk.project.goodsstorage.customer.CustomerIdWrapper;
import tk.project.goodsstorage.customer.repository.CustomerRepository;
import tk.project.goodsstorage.orchestrator.OrchestratorProvider;
import tk.project.goodsstorage.orchestrator.dto.ConfirmOrderDto;
import tk.project.goodsstorage.order.dto.SaveOrderedProductDto;
import tk.project.goodsstorage.order.dto.create.CreateOrderDto;
import tk.project.goodsstorage.order.dto.find.FindOrderDto;
import tk.project.goodsstorage.order.dto.find.FindOrderedProductDto;
import tk.project.goodsstorage.order.dto.update.SetOrderStatusRequest;
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
    private final CustomerIdWrapper customerIdWrapper;
    private final CustomerRepository customerRepository;
    private final OrchestratorProvider orchestratorProvider;
    private final OrderDtoMapper mapper;
    private final OrderedProductRepository orderedProductRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public UUID create(final CreateOrderDto orderDto) {
        final Customer customer = getCustomerById(customerIdWrapper.getCustomerId());
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
    public UUID confirmById(final UUID orderId) {
        final Order order = getOrderByIdFetch(orderId);
        checkCustomerAccessToOrder(order);
        checkOrderStatusIsCreate(order);

        final ConfirmOrderDto confirmOrderDto = ConfirmOrderDto.builder()
                .id(order.getId())
                .deliveryAddress(order.getDeliveryAddress())
                .customerLogin(order.getCustomer().getLogin())
                .price(order.getProducts().stream()
                        .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getCount())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();

        UUID businessKey = orchestratorProvider.confirmOrder(confirmOrderDto);

        order.setBusinessKey(businessKey);
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
        return businessKey;
    }

    @Override
    public FindOrderDto findById(UUID orderId) {
        final Order order = getOrderByIdFetch(orderId);
        checkCustomerAccessToOrder(order);

        final List<FindOrderedProductDto> orderedProductDto =
                orderedProductRepository.findAllWithNameByOrderId(order.getId());
        BigDecimal totalPrice = orderedProductDto.stream()
                .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return FindOrderDto.builder()
                .orderId(order.getId())
                .products(orderedProductDto)
                .totalPrice(totalPrice)
                .build();
    }

    @Transactional
    @Override
    public UpdateOrderDtoRes update(UpdateOrderDto updateOrderDto, UUID orderId) {
        Order oldOrder = getOrderByIdFetch(orderId);
        checkCustomerAccessToOrder(oldOrder);
        checkOrderStatusIsCreate(oldOrder);

        if (Objects.nonNull(updateOrderDto.getDeliveryAddress())) {
            oldOrder.setDeliveryAddress(updateOrderDto.getDeliveryAddress());
        }

        final Set<UpdateOrderedProductDto> updateOrderedProductsDto = updateOrderDto.getProducts();
        if (Objects.nonNull(updateOrderedProductsDto) && !updateOrderedProductsDto.isEmpty()) {
            addOrderedProducts(updateOrderedProductsDto, oldOrder);
        }

        orderRepository.save(oldOrder);
        return mapper.toUpdateOrderDtoRes(oldOrder);
    }

    @Transactional
    @Override
    public UpdateOrderStatusDto setStatusDone(final UUID orderId) {
        Order order = getOrderByIdFetch(orderId);
        checkCustomerAccessToOrder(order);
        checkOrderStatusIsNotCancelled(order);
        checkOrderStatusIsNotRejected(order);
        order.setStatus(OrderStatus.DONE);
        orderRepository.save(order);
        return new UpdateOrderStatusDto(order.getStatus());
    }

    @Override
    public UpdateOrderStatusDto setStatusByOrchestrator(final SetOrderStatusRequest statusRequest) {
        Order order = getOrderByIdFetch(statusRequest.getOrderId());
        checkOrderStatusIsProcessing(order);
        order.setStatus(statusRequest.getStatus());
        order.setDeliveryDate(statusRequest.getDeliveryDate());
        orderRepository.save(order);
        return new UpdateOrderStatusDto(order.getStatus());
    }

    @Transactional
    @Override
    public void deleteById(final UUID orderId) {
        Order order = getOrderByIdFetch(orderId);
        checkCustomerAccessToOrder(order);
        checkOrderStatusIsCreate(order);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        cancelOrderedProducts(order);
    }

    private void addOrderedProducts(final Set<? extends SaveOrderedProductDto> orderedProductsDto, Order order) {
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

    private Order getOrderByIdFetch(final UUID orderId) {
        return orderRepository.findByIdFetch(orderId).orElseThrow(() -> throwOrderNotFoundException(orderId));
    }

    private OrderNotFoundException throwOrderNotFoundException(final UUID orderId) {
        final String message = String.format("Order was not found by id: %s", orderId);
        log.warn(message);
        return new OrderNotFoundException(message);
    }

    private Customer getCustomerById(final long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> {
            final String message = String.format("Customer was not found by id: %s", customerId);
            log.warn(message);
            return new CustomerNotFoundException(message);
        });
    }

    private void checkProductIsAvailable(final Product product) {
        if (!product.getIsAvailable()) {
            final String message = String.format("Product with id = %s is not available", product.getId());
            log.warn(message);
            throw new ProductNotAvailableException(message);
        }
    }

    private void checkOrderStatusIsCreate(final Order order) {
        if (order.getStatus() != OrderStatus.CREATED) {
            final String message = String.format("Order status must be CREATE but now status: %s", order.getStatus());
            log.warn(message);
            throw new OrderStatusNotCreateException(message);
        }
    }

    private void checkOrderStatusIsNotCancelled(final Order order) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            final String message = String.format("Order status has already been %s", OrderStatus.CANCELLED);
            log.warn(message);
            throw new OrderStatusAlreadyCancelledException(message);
        }
    }

    private void checkOrderStatusIsNotRejected(final Order order) {
        if (order.getStatus() == OrderStatus.REJECTED) {
            final String message = String.format("Order status has already been %s", OrderStatus.REJECTED);
            log.warn(message);
            throw new OrderStatusAlreadyRejectedException(message);
        }
    }

    private void checkOrderStatusIsProcessing(final Order order) {
        if (order.getStatus() != OrderStatus.PROCESSING) {
            final String message = String.format("Order status must be %s", OrderStatus.PROCESSING);
            log.warn(message);
            throw new OrderStatusNotProcessingException(message);
        }
    }

    private void checkCustomerAccessToOrder(final Order order) {
        if (!order.getCustomer().getId().equals(customerIdWrapper.getCustomerId())) {
            String message = "No access to order";
            log.warn(message);
            throw new OrderNotAccessException(message);
        }
    }

    private void cancelOrderedProducts(final Order order) {
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
}
