package tk.project.goodsstorage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tk.project.goodsstorage.BaseIntegrationTest;
import tk.project.goodsstorage.BaseWireMockTest;
import tk.project.goodsstorage.dto.orchestrator.OrchestratorConfirmOrderResponse;
import tk.project.goodsstorage.enums.OrderStatus;
import tk.project.goodsstorage.exceptionhandler.exceptions.ApiError;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.RequestConfirmOrderToOrchestratorException;
import tk.project.goodsstorage.models.Customer;
import tk.project.goodsstorage.models.order.Order;
import tk.project.goodsstorage.models.order.OrderedProduct;
import tk.project.goodsstorage.models.product.Product;
import tk.project.goodsstorage.models.product.objectmother.ProductMother;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConfirmOrderIntegrationTest extends BaseIntegrationTest implements BaseWireMockTest {
    @RegisterExtension
    private static final WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort().dynamicPort())
            .build();

    @DynamicPropertySource
    private static void setWireMockExtension(DynamicPropertyRegistry registry) {
        registry.add("account-service.host", wireMockExtension::baseUrl);
        registry.add("crm-service.host", wireMockExtension::baseUrl);
        registry.add("orchestrator-goods-storage.host", wireMockExtension::baseUrl);
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
    @DisplayName("Confirm order successfully")
    void confirmOrder() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice());

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        addWireMockExtensionStubPost("/crm-service/inns", Map.of(customer.getLogin(), "123456"));
        addWireMockExtensionStubPost("/account-service/numbers", Map.of(customer.getLogin(), "789"));

        final UUID businessKey = UUID.randomUUID();
        addWireMockExtensionStubPost("/orc-gs/confirm-order",
                OrchestratorConfirmOrderResponse.builder().businessKey(businessKey).build());

        // WHEN
        final String result = mockMvc.perform(post(String.format("/order/%s/confirm", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertNotNull(result);
        assertTrue(result.contains(businessKey.toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to confirm order when crm service did not response correctly")
    void confirmOrderFailedWithRequestFindInnException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice());

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        addWireMockExtensionStubPost(HttpStatus.NOT_FOUND, "/crm-service/inns", Map.of());
        addWireMockExtensionStubPost("/account-service/numbers", Map.of(customer.getLogin(), "789"));

        final UUID businessKey = UUID.randomUUID();
        addWireMockExtensionStubPost("/orc-gs/confirm-order",
                OrchestratorConfirmOrderResponse.builder().businessKey(businessKey).build());

        // WHEN
        mockMvc.perform(post(String.format("/order/%s/confirm", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                // THEN
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to confirm order when account service did not response correctly")
    void confirmOrderFailedWithRequestFindAccountNumberException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice());

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        addWireMockExtensionStubPost("/crm-service/inns", Map.of(customer.getLogin(), "123456"));
        addWireMockExtensionStubPost(HttpStatus.NOT_FOUND, "/account-service/numbers", Map.of());

        final UUID businessKey = UUID.randomUUID();
        addWireMockExtensionStubPost("/orc-gs/confirm-order",
                OrchestratorConfirmOrderResponse.builder().businessKey(businessKey).build());

        // WHEN
        mockMvc.perform(post(String.format("/order/%s/confirm", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                // THEN
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to confirm order when orchestrator did not response correctly")
    void confirmOrderWithRequestConfirmOrderToOrchestratorException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice());

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        addWireMockExtensionStubPost("/crm-service/inns", Map.of(customer.getLogin(), "123456"));
        addWireMockExtensionStubPost("/account-service/numbers", Map.of(customer.getLogin(), "789"));

        addWireMockExtensionStubPost(HttpStatus.BAD_REQUEST, "/orc-gs/confirm-order",
                OrchestratorConfirmOrderResponse.builder().businessKey(null).build());

        // WHEN
        final String result = mockMvc.perform(post(String.format("/order/%s/confirm", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(RequestConfirmOrderToOrchestratorException.class.getSimpleName(), apiError.getExceptionName());
    }
}