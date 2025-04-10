package tk.project.goodsstorage.order.info;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.customer.Customer;
import tk.project.goodsstorage.customer.dto.find.CustomerInfo;
import tk.project.goodsstorage.customer.info.CustomerInfoProvider;
import tk.project.goodsstorage.order.model.Order;
import tk.project.goodsstorage.order.model.OrderStatus;
import tk.project.goodsstorage.order.model.OrderedProduct;
import tk.project.goodsstorage.order.repository.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderInfoServiceImpl implements OrderInfoService{
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
                            OrderInfo orderInfo = new OrderInfo();
                            orderInfo.setId(order.getId());
                            orderInfo.setCustomer(customerInfoMap.get(order.getCustomer().getId()));
                            orderInfo.setStatus(order.getStatus());
                            orderInfo.setDeliveryAddress(order.getDeliveryAddress());
                            orderInfo.setCount(orderedProduct.getCount());
                            return orderInfo;
                        }, Collectors.toList())));
    }
}
