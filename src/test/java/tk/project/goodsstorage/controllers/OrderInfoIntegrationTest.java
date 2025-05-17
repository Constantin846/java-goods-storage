package tk.project.goodsstorage.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tk.project.goodsstorage.BaseIntegrationTest;
import tk.project.goodsstorage.BaseWireMockTest;
import tk.project.goodsstorage.dto.order.find.OrderInfo;
import tk.project.goodsstorage.enums.OrderStatus;
import tk.project.goodsstorage.models.Customer;
import tk.project.goodsstorage.models.order.Order;
import tk.project.goodsstorage.models.order.OrderedProduct;
import tk.project.goodsstorage.models.product.Product;
import tk.project.goodsstorage.models.product.objectmother.ProductMother;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderInfoIntegrationTest extends BaseIntegrationTest implements BaseWireMockTest {
    @RegisterExtension
    private static final WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort().dynamicPort())
            .build();

    @DynamicPropertySource
    private static void setWireMockExtension(DynamicPropertyRegistry registry) {
        registry.add("account-service.host", wireMockExtension::baseUrl);
        registry.add("crm-service.host", wireMockExtension::baseUrl);
    }

    @Override
    public WireMockExtension getWireMockExtension() {
        return wireMockExtension;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Test
    @SneakyThrows
    @DisplayName("Find order info sorting by product id successfully")
    void findProductIdOrdersInfo() {
        // GIVEN
        final OrderStatus expectedOrderStatus = OrderStatus.CREATED;
        final String expectedAddress = "address";
        final Long expectedCount = 1L;
        final String expectedSecondAddress = "second address";
        final String expectedEmail = "em@mail.com";

        Customer customer = new Customer(1L, "login", expectedEmail, true);
        customer = customerRepository.save(customer);
        final Long expectedCustomerId = customer.getId();

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(expectedCount);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(expectedOrderStatus);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);
        final UUID expectedOrderId = order.getId();

        Order secondOrder = new Order();
        secondOrder.setDeliveryAddress(expectedSecondAddress);
        secondOrder.setProducts(Set.of(orderedProduct));
        secondOrder.setStatus(expectedOrderStatus);
        secondOrder.setCustomer(customer);

        orderedProduct.setOrder(secondOrder);
        secondOrder = orderRepository.save(secondOrder);
        final UUID expectedSecondOrderId = secondOrder.getId();

        final String expectedInn = "123456";
        final String expectedAccountNumber = "789";
        addWireMockExtensionStubPost("/crm-service/inns", Map.of(customer.getLogin(), expectedInn));
        addWireMockExtensionStubPost("/account-service/numbers", Map.of(customer.getLogin(), expectedAccountNumber));

        // WHEN
        final String result = mockMvc.perform(get("/order/info/group-by-product-id")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final Map<UUID, List<OrderInfo>> productIdOrderInfoMap = objectMapper
                .readValue(result, new TypeReference<Map<UUID, List<OrderInfo>>>(){});

        // THEN
        assertTrue(productIdOrderInfoMap.containsKey(product.getId()));
        assertThat(productIdOrderInfoMap.get(product.getId()))
                .anySatisfy(orderInfo -> {
                    assertEquals(expectedOrderId, orderInfo.getId());
                    assertEquals(expectedOrderStatus, orderInfo.getStatus());
                    assertEquals(expectedAddress, orderInfo.getDeliveryAddress());
                    assertEquals(expectedCount, orderInfo.getCount());

                    assertEquals(expectedCustomerId, orderInfo.getCustomer().getId());
                    assertEquals(expectedEmail, orderInfo.getCustomer().getEmail());
                    assertEquals(expectedInn, orderInfo.getCustomer().getInn());
                    assertEquals(expectedAccountNumber, orderInfo.getCustomer().getAccountNumber());
                })
                .anySatisfy(orderInfo -> {
                    assertEquals(expectedSecondOrderId, orderInfo.getId());
                    assertEquals(expectedOrderStatus, orderInfo.getStatus());
                    assertEquals(expectedSecondAddress, orderInfo.getDeliveryAddress());
                    assertEquals(expectedCount, orderInfo.getCount());

                    assertEquals(expectedCustomerId, orderInfo.getCustomer().getId());
                    assertEquals(expectedEmail, orderInfo.getCustomer().getEmail());
                    assertEquals(expectedInn, orderInfo.getCustomer().getInn());
                    assertEquals(expectedAccountNumber, orderInfo.getCustomer().getAccountNumber());
                })
                .hasSize(2);
    }
}