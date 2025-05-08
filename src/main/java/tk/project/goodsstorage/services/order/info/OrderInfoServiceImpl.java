package tk.project.goodsstorage.services.order.info;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.dto.customer.find.CustomerInfo;
import tk.project.goodsstorage.dto.order.find.OrderInfo;
import tk.project.goodsstorage.enums.OrderStatus;
import tk.project.goodsstorage.models.Customer;
import tk.project.goodsstorage.models.order.Order;
import tk.project.goodsstorage.models.order.OrderedProduct;
import tk.project.goodsstorage.repositories.order.OrderRepository;
import tk.project.goodsstorage.services.customer.CustomerInfoProvider;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderInfoServiceImpl implements OrderInfoService {
    private final CustomerInfoProvider customerInfoProvider;
    private final OrderRepository orderRepository;

    @Override
    public Map<UUID, List<OrderInfo>> findProductIdOrdersInfo() {
        List<Order> orders = orderRepository.findByOrderStatus(List.of(OrderStatus.CREATED, OrderStatus.CONFIRMED));
        Set<Customer> customers = orders.stream()
                .map(Order::getCustomer)
                .collect(Collectors.toSet());

        Map<Long, CustomerInfo> customerInfoMap = customerInfoProvider.getIdCustomerInfoMap(customers);

        return orders.stream()
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.groupingBy(OrderedProduct::getProductId,
                        Collectors.mapping(orderedProduct -> {
                            Order order = orderedProduct.getOrder();
                            return OrderInfo.builder()
                                    .id(order.getId())
                                    .customer(customerInfoMap.get(order.getCustomer().getId()))
                                    .status(order.getStatus())
                                    .deliveryAddress(order.getDeliveryAddress())
                                    .count(orderedProduct.getCount())
                                    .build();
                        }, Collectors.toList())));
    }
}
