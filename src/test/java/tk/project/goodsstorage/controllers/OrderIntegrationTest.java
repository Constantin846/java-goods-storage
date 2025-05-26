package tk.project.goodsstorage.controllers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import tk.project.goodsstorage.BaseIntegrationTest;
import tk.project.goodsstorage.dto.order.create.CreateOrderRequest;
import tk.project.goodsstorage.dto.order.create.CreateOrderedProductRequest;
import tk.project.goodsstorage.dto.order.find.FindOrderResponse;
import tk.project.goodsstorage.dto.order.find.FindOrderedProductResponse;
import tk.project.goodsstorage.dto.order.update.SetOrderStatusRequest;
import tk.project.goodsstorage.dto.order.update.UpdateOrderRequest;
import tk.project.goodsstorage.dto.order.update.UpdateOrderResponse;
import tk.project.goodsstorage.dto.order.update.UpdateOrderStatusDto;
import tk.project.goodsstorage.dto.order.update.UpdateOrderedProductRequest;
import tk.project.goodsstorage.enums.OrderStatus;
import tk.project.goodsstorage.exceptionhandler.exceptions.ApiError;
import tk.project.goodsstorage.exceptionhandler.exceptions.customer.CustomerNotFoundException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderNotAccessException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderNotFoundException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderStatusAlreadyCancelledException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderStatusAlreadyRejectedException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderStatusNotCreatedException;
import tk.project.goodsstorage.exceptionhandler.exceptions.order.OrderStatusNotProcessingException;
import tk.project.goodsstorage.exceptionhandler.exceptions.product.ProductCountNotEnoughException;
import tk.project.goodsstorage.exceptionhandler.exceptions.product.ProductNotAvailableException;
import tk.project.goodsstorage.exceptionhandler.exceptions.product.ProductsNotFoundByIdsException;
import tk.project.goodsstorage.models.Customer;
import tk.project.goodsstorage.models.order.Order;
import tk.project.goodsstorage.models.order.OrderedProduct;
import tk.project.goodsstorage.models.product.Product;
import tk.project.goodsstorage.models.product.objectmother.ProductMother;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderIntegrationTest extends BaseIntegrationTest {

    @Test
    @SneakyThrows
    @DisplayName("Create order successfully")
    void createOrder() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        final CreateOrderedProductRequest createProductRequest = CreateOrderedProductRequest.builder()
                .id(product.getId())
                .count(1L)
                .build();
        final CreateOrderRequest orderRequest = CreateOrderRequest.builder()
                .deliveryAddress("address")
                .products(Set.of(createProductRequest))
                .build();

        // WHEN
        final String result = mockMvc.perform(post("/order")
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertNotNull(result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to create order when customer not found")
    void createOrderExceptionCustomerNotFound() {
        // GIVEN
        final Long customerId = 1L;

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        final CreateOrderedProductRequest createProductRequest = CreateOrderedProductRequest.builder()
                .id(product.getId())
                .count(1L)
                .build();
        final CreateOrderRequest orderRequest = CreateOrderRequest.builder()
                .deliveryAddress("address")
                .products(Set.of(createProductRequest))
                .build();

        // WHEN
        final String result = mockMvc.perform(post("/order")
                        .header("X-Customer-ID", customerId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(CustomerNotFoundException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(customerId.toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to create order when products not found")
    void createOrderExceptionProductsNotFoundByIds() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        final UUID firstProductId = UUID.randomUUID();
        final UUID secondProductId = UUID.randomUUID();

        final CreateOrderedProductRequest firstProductRequest = CreateOrderedProductRequest.builder()
                .id(firstProductId)
                .count(1L)
                .build();
        final CreateOrderedProductRequest secondProductRequest = CreateOrderedProductRequest.builder()
                .id(secondProductId)
                .count(1L)
                .build();
        final CreateOrderRequest orderRequest = CreateOrderRequest.builder()
                .deliveryAddress("address")
                .products(Set.of(firstProductRequest, secondProductRequest))
                .build();

        // WHEN
        final String result = mockMvc.perform(post("/order")
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(ProductsNotFoundByIdsException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(firstProductId.toString()));
        assertTrue(apiError.getMessage().contains(secondProductId.toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to create order when product is not available")
    void createOrderExceptionProductIsNotAvailable() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().withIsAvailable(false).build();
        product = productRepository.save(product);

        final CreateOrderedProductRequest createProductRequest = CreateOrderedProductRequest.builder()
                .id(product.getId())
                .count(1L)
                .build();
        final CreateOrderRequest orderRequest = CreateOrderRequest.builder()
                .deliveryAddress("address")
                .products(Set.of(createProductRequest))
                .build();

        // WHEN
        final String result = mockMvc.perform(post("/order")
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(ProductNotAvailableException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(product.getId().toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to create order when product count is not enough")
    void createOrderExceptionProductCountIsNotEnough() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().withCount(1L).build();
        product = productRepository.save(product);

        final CreateOrderedProductRequest createProductRequest = CreateOrderedProductRequest.builder()
                .id(product.getId())
                .count(2L)
                .build();
        final CreateOrderRequest orderRequest = CreateOrderRequest.builder()
                .deliveryAddress("address")
                .products(Set.of(createProductRequest))
                .build();

        // WHEN
        final String result = mockMvc.perform(post("/order")
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(ProductCountNotEnoughException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(product.getId().toString()));
        assertTrue(apiError.getMessage().contains(product.getCount().toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Find order by id successfully")
    void findOrderById() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        final FindOrderResponse expectedResponse = FindOrderResponse.builder()
                .orderId(order.getId())
                .products(List.of(
                        FindOrderedProductResponse.builder()
                                .productId(product.getId())
                                .name(product.getName())
                                .price(orderedProduct.getPrice().setScale(2, RoundingMode.HALF_EVEN))
                                .count(orderedProduct.getCount())
                                .build()
                ))
                .totalPrice(orderedProduct.getPrice().setScale(2, RoundingMode.HALF_EVEN))
                .build();

        // WHEN
        final String result = mockMvc.perform(get(String.format("/order/%s", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertEquals(objectMapper.writeValueAsString(expectedResponse), result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to find order by id when order was not found")
    void findOrderByIdWithOrderNotFoundException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        final UUID orderId = UUID.randomUUID();

        // WHEN
        final String result = mockMvc.perform(get(String.format("/order/%s", orderId))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(OrderNotFoundException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(orderId.toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to find order by id when customer does not have access")
    void findOrderByIdWithOrderNotAccessException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Customer otherCustomer = new Customer(2L, "other", "em@mail.com", true);
        otherCustomer = customerRepository.save(otherCustomer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        // WHEN
        final String result = mockMvc.perform(get(String.format("/order/%s", order.getId()))
                        .header("X-Customer-ID", otherCustomer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(OrderNotAccessException.class.getSimpleName(), apiError.getExceptionName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Update order successfully")
    void updateOrder() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        Product productSecond = ProductMother.createDefaultProduct().build();
        productSecond = productRepository.save(productSecond);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        final UpdateOrderedProductRequest updateProductRequest = UpdateOrderedProductRequest.builder()
                .id(product.getId())
                .count(1L)
                .build();
        final UpdateOrderedProductRequest newProductRequest = UpdateOrderedProductRequest.builder()
                .id(productSecond.getId())
                .count(1L)
                .build();
        final UpdateOrderRequest orderRequest = UpdateOrderRequest.builder()
                .deliveryAddress("update address")
                .products(Set.of(updateProductRequest, newProductRequest))
                .build();

        final UUID updateOrderedProductResponseId = updateProductRequest.getId();
        final Long updateOrderedProductResponseCount = orderedProduct.getCount() + updateProductRequest.getCount();
        final UUID newUpdateOrderedProductResponseId = newProductRequest.getId();
        final Long newUpdateOrderedProductResponseCount = newProductRequest.getCount();

        final UpdateOrderResponse expectedResponse = UpdateOrderResponse.builder()
                .id(order.getId())
                .deliveryAddress(orderRequest.getDeliveryAddress())
                .build();

        // WHEN
        final String result = mockMvc.perform(patch(String.format("/order/%s", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final UpdateOrderResponse actualResponse = objectMapper.readValue(result, UpdateOrderResponse.class);

        // THEN
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getDeliveryAddress(), actualResponse.getDeliveryAddress());
        assertThat(actualResponse.getProducts())
                .anySatisfy(orderedProductResponse -> {
                    assertEquals(updateOrderedProductResponseId, orderedProductResponse.getProductId());
                    assertEquals(updateOrderedProductResponseCount, orderedProductResponse.getCount());
                })
                .anySatisfy(orderedProductResponse -> {
                    assertEquals(newUpdateOrderedProductResponseId, orderedProductResponse.getProductId());
                    assertEquals(newUpdateOrderedProductResponseCount, orderedProductResponse.getCount());
                })
                .hasSize(2);
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to update order when order status is not created")
    void updateOrderWithOrderStatusNotCreatedException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CANCELLED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        final UpdateOrderedProductRequest updateProductRequest = UpdateOrderedProductRequest.builder()
                .id(product.getId())
                .count(1L)
                .build();
        final UpdateOrderRequest orderRequest = UpdateOrderRequest.builder()
                .deliveryAddress("update address")
                .products(Set.of(updateProductRequest))
                .build();

        // WHEN
        final String result = mockMvc.perform(patch(String.format("/order/%s", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(OrderStatusNotCreatedException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(order.getStatus().toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Set order status DONE successfully")
    void setOrderStatusDone() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        final UpdateOrderStatusDto expectedResponse = UpdateOrderStatusDto.builder()
                .status(OrderStatus.DONE)
                .build();

        // WHEN
        final String result = mockMvc.perform(patch(String.format("/order/%s/status", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertEquals(objectMapper.writeValueAsString(expectedResponse), result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to set order status DONE when order status has already CANCELLED")
    void setOrderStatusDoneWithOrderStatusAlreadyCancelledException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CANCELLED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        // WHEN
        final String result = mockMvc.perform(patch(String.format("/order/%s/status", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(OrderStatusAlreadyCancelledException.class.getSimpleName(), apiError.getExceptionName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to set order status DONE when order status has already REJECTED")
    void setOrderStatusDoneWithOrderStatusAlreadyRejectedException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.REJECTED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        // WHEN
        final String result = mockMvc.perform(patch(String.format("/order/%s/status", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(OrderStatusAlreadyRejectedException.class.getSimpleName(), apiError.getExceptionName());
    }

    @Value("${orchestrator-goods-storage.orchestrator-id}")
    private String orchestratorId;

    @Test
    @SneakyThrows
    @DisplayName("Set status by orchestrator successfully")
    void setStatusByOrchestrator() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.PROCESSING);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        final SetOrderStatusRequest request = SetOrderStatusRequest.builder()
                .orderId(order.getId())
                .status(OrderStatus.CONFIRMED)
                .deliveryDate(LocalDate.now())
                .build();

        final UpdateOrderStatusDto expectedResponse = UpdateOrderStatusDto.builder()
                .status(request.getStatus())
                .build();

        // WHEN
        final String result = mockMvc.perform(patch("/order/set-status")
                        .header("X-Customer-ID", customer.getId())
                        .header("X_Orchestrator_ID", orchestratorId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertEquals(objectMapper.writeValueAsString(expectedResponse), result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to set status by orchestrator when orchestrator does not have not access")
    void setStatusByOrchestratorWithOrderNotAccessException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.PROCESSING);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        final SetOrderStatusRequest request = SetOrderStatusRequest.builder()
                .orderId(order.getId())
                .status(OrderStatus.CONFIRMED)
                .deliveryDate(LocalDate.now())
                .build();

        final String otherOrchestratorId = "other" + orchestratorId;

        // WHEN
        final String result = mockMvc.perform(patch("/order/set-status")
                        .header("X-Customer-ID", customer.getId())
                        .header("X_Orchestrator_ID", otherOrchestratorId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        assertEquals(OrderNotAccessException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(otherOrchestratorId));
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to set status by orchestrator when order status is not processing")
    void setStatusByOrchestratorWithOrderStatusNotProcessingException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        final SetOrderStatusRequest request = SetOrderStatusRequest.builder()
                .orderId(order.getId())
                .status(OrderStatus.CONFIRMED)
                .deliveryDate(LocalDate.now())
                .build();

        // WHEN
        final String result = mockMvc.perform(patch("/order/set-status")
                        .header("X-Customer-ID", customer.getId())
                        .header("X_Orchestrator_ID", orchestratorId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(OrderStatusNotProcessingException.class.getSimpleName(), apiError.getExceptionName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Delete order by id successfully")
    void deleteOrderById() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Long productCount = 111L;
        Product product = ProductMother.createDefaultProduct().withCount(productCount).build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        // WHEN
        mockMvc.perform(delete(String.format("/order/%s", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertEquals(OrderStatus.CANCELLED, orderRepository.findById(order.getId()).get().getStatus());
        assertEquals(productCount + orderedProduct.getCount(),
                productRepository.findById(product.getId()).get().getCount());
    }

    @Test
    @SneakyThrows
    @DisplayName("Delete order by id successfully if product does not exist")
    void deleteOrderByIdIfProductDoesNotExist() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Long productCount = 111L;
        Product product = ProductMother.createDefaultProduct().withCount(productCount).build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        productRepository.deleteById(product.getId());

        // WHEN
        mockMvc.perform(delete(String.format("/order/%s", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertEquals(OrderStatus.CANCELLED, orderRepository.findById(order.getId()).get().getStatus());
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to delete order by id when customer does not have access")
    void deleteOrderByWithOrderNotAccessException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Customer otherCustomer = new Customer(2L, "other login", "em@mail.com", true);
        otherCustomer = customerRepository.save(otherCustomer);

        Long productCount = 111L;
        Product product = ProductMother.createDefaultProduct().withCount(productCount).build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        // WHEN
        final String result = mockMvc.perform(delete(String.format("/order/%s", order.getId()))
                        .header("X-Customer-ID", otherCustomer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(OrderNotAccessException.class.getSimpleName(), apiError.getExceptionName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to delete order by id when order status is not created")
    void deleteOrderByWithOrderStatusNotCreatedException() {
        // GIVEN
        Customer customer = new Customer(1L, "login", "em@mail.com", true);
        customer = customerRepository.save(customer);

        Long productCount = 111L;
        Product product = ProductMother.createDefaultProduct().withCount(productCount).build();
        product = productRepository.save(product);

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(product.getId());
        orderedProduct.setCount(1L);
        orderedProduct.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_EVEN));

        Order order = new Order();
        order.setDeliveryAddress("address");
        order.setProducts(Set.of(orderedProduct));
        order.setStatus(OrderStatus.PROCESSING);
        order.setCustomer(customer);

        orderedProduct.setOrder(order);
        order = orderRepository.save(order);

        // WHEN
        final String result = mockMvc.perform(delete(String.format("/order/%s", order.getId()))
                        .header("X-Customer-ID", customer.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(OrderStatusNotCreatedException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(order.getStatus().toString()));
    }
}