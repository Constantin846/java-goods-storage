package tk.project.goodsstorage.services.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.dto.orchestrator.ConfirmOrderDto;
import tk.project.goodsstorage.dto.order.create.CreateOrderDto;
import tk.project.goodsstorage.dto.order.find.FindOrderDto;
import tk.project.goodsstorage.dto.order.find.FindOrderedProductDto;
import tk.project.goodsstorage.dto.order.update.SetOrderStatusRequest;
import tk.project.goodsstorage.dto.order.update.UpdateOrderDto;
import tk.project.goodsstorage.dto.order.update.UpdateOrderDtoRes;
import tk.project.goodsstorage.dto.order.update.UpdateOrderStatusDto;
import tk.project.goodsstorage.dto.order.update.UpdateOrderedProductDto;
import tk.project.goodsstorage.enums.OrderStatus;
import tk.project.goodsstorage.exceptionhandler.exceptions.customer.CustomerNotFoundException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderNotAccessException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderNotFoundException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderStatusAlreadyCancelledException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderStatusAlreadyRejectedException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderStatusNotCreatedException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderStatusNotProcessingException;
import tk.project.goodsstorage.headerfilter.CustomerIdWrapper;
import tk.project.goodsstorage.mappers.OrderDtoMapper;
import tk.project.goodsstorage.models.Customer;
import tk.project.goodsstorage.models.order.Order;
import tk.project.goodsstorage.repositories.CustomerRepository;
import tk.project.goodsstorage.repositories.order.OrderRepository;
import tk.project.goodsstorage.repositories.order.OrderedProductRepository;
import tk.project.goodsstorage.services.orchestrator.OrchestratorService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CustomerIdWrapper customerIdWrapper;
    private final CustomerRepository customerRepository;
    private final OrchestratorService orchestratorService;
    private final OrderDtoMapper mapper;
    private final OrderedProductRepository orderedProductRepository;
    private final OrderRepository orderRepository;
    private final OrderProductServiceImpl orderProductService;

    @Transactional
    @Override
    public UUID create(final CreateOrderDto orderDto) {
        final Customer customer = getCustomerById(customerIdWrapper.getCustomerId());
        Order order = mapper.toOrder(orderDto);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);
        order.setProducts(new HashSet<>());

        order = orderProductService.addOrderedProducts(orderDto.getProducts(), order);
        order = orderRepository.save(order);
        return order.getId();
    }

    @Transactional
    @Override
    public UUID confirmById(final UUID orderId) {
        final Order order = getOrderByIdFetch(orderId);
        checkCustomerAccessToOrder(order);
        checkOrderStatusIsCreated(order);

        final ConfirmOrderDto confirmOrderDto = ConfirmOrderDto.builder()
                .id(order.getId())
                .deliveryAddress(order.getDeliveryAddress())
                .customerLogin(order.getCustomer().getLogin())
                .price(order.getProducts().stream()
                        .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getCount())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();

        UUID businessKey = orchestratorService.confirmOrder(confirmOrderDto);

        order.setBusinessKey(businessKey);
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
        return businessKey;
    }

    @Override
    public FindOrderDto findById(final UUID orderId) {
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
    public UpdateOrderDtoRes update(final UpdateOrderDto updateOrderDto, final UUID orderId) {
        Order oldOrder = getOrderByIdFetch(orderId);
        checkCustomerAccessToOrder(oldOrder);
        checkOrderStatusIsCreated(oldOrder);

        if (Objects.nonNull(updateOrderDto.getDeliveryAddress())) {
            oldOrder.setDeliveryAddress(updateOrderDto.getDeliveryAddress());
        }

        final Set<UpdateOrderedProductDto> updateOrderedProductsDto = updateOrderDto.getProducts();
        if (Objects.nonNull(updateOrderedProductsDto) && !updateOrderedProductsDto.isEmpty()) {
            oldOrder = orderProductService.addOrderedProducts(updateOrderedProductsDto, oldOrder);
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

    @Transactional
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
        checkOrderStatusIsCreated(order);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        orderProductService.cancelOrderedProducts(order);
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

    private void checkOrderStatusIsCreated(final Order order) {
        if (order.getStatus() != OrderStatus.CREATED) {
            final String message = String.format("Order status must be CREATED but now status: %s", order.getStatus());
            log.warn(message);
            throw new OrderStatusNotCreatedException(message);
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
            final String message = "No access to order";
            log.warn(message);
            throw new OrderNotAccessException(message);
        }
    }
}
