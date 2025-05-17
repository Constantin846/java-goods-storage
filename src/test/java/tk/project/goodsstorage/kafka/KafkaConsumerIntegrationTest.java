package tk.project.goodsstorage.kafka;

import lombok.SneakyThrows;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import tk.project.goodsstorage.BaseIntegrationTest;
import tk.project.goodsstorage.KafkaTestContainer;
import tk.project.goodsstorage.dto.kafka.event.order.CreateOrderEventData;
import tk.project.goodsstorage.dto.kafka.event.order.DeleteOrderEventData;
import tk.project.goodsstorage.dto.kafka.event.order.SetOrderStatusDoneEventData;
import tk.project.goodsstorage.dto.kafka.event.order.UpdateOrderEventData;
import tk.project.goodsstorage.dto.order.create.CreateOrderDto;
import tk.project.goodsstorage.dto.order.create.CreateOrderedProductDto;
import tk.project.goodsstorage.dto.order.update.UpdateOrderDto;
import tk.project.goodsstorage.dto.order.update.UpdateOrderedProductDto;
import tk.project.goodsstorage.enums.Event;
import tk.project.goodsstorage.services.order.OrderService;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;

public class KafkaConsumerIntegrationTest extends BaseIntegrationTest implements KafkaTestContainer {
    @Autowired
    private KafkaProducer kafkaProducer;
    @MockBean
    private OrderService orderServiceMock;

    @Test
    @SneakyThrows
    @DisplayName("Call create method of OrderService by kafka request")
    void createOrderByKafkaRequest() {
        // GIVEN
        final UUID productId = UUID.randomUUID();
        final CreateOrderedProductDto createProductDto = new CreateOrderedProductDto(productId, 1L);

        final CreateOrderDto orderDto = CreateOrderDto.builder()
                .deliveryAddress("address")
                .products(Set.of(createProductDto))
                .build();
        final CreateOrderEventData data = CreateOrderEventData.builder()
                .orderDto(orderDto)
                .event(Event.CREATE_ORDER)
                .build();

        // WHEN
        kafkaProducer.sendEvent(kafkaProducer.getOrderCommandTopic(), data.getOrderDto().getDeliveryAddress(), data);

        // THEN
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(2, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(orderServiceMock).create(orderDto));
    }

    @Test
    @SneakyThrows
    @DisplayName("Call update method of OrderService by kafka request")
    void updateOrderByKafkaRequest() {
        // GIVEN
        final UUID productId = UUID.randomUUID();
        final UUID productIdSecond = UUID.randomUUID();

        final UpdateOrderedProductDto updateProductDto = new UpdateOrderedProductDto(productId, 1L);
        final UpdateOrderedProductDto updateProductDtoSecond = new UpdateOrderedProductDto(productIdSecond, 1L);

        final UUID orderId = UUID.randomUUID();
        final UpdateOrderDto orderDto = UpdateOrderDto.builder()
                .id(orderId)
                .deliveryAddress("update address")
                .products(Set.of(updateProductDto, updateProductDtoSecond))
                .build();

        final UpdateOrderEventData data = UpdateOrderEventData.builder()
                .orderDto(orderDto)
                .event(Event.UPDATE_ORDER)
                .build();

        // WHEN
        kafkaProducer.sendEvent(kafkaProducer.getOrderCommandTopic(), data.getOrderDto().getDeliveryAddress(), data);

        // THEN
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(2, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(orderServiceMock).update(orderDto, orderId));
    }

    @Test
    @SneakyThrows
    @DisplayName("Call setStatusDone method of OrderService by kafka request")
    void setOrderStatusDoneByKafkaRequest() {
        // GIVEN
        final UUID orderId = UUID.randomUUID();
        final Long customerId = 1L;

        final SetOrderStatusDoneEventData data = SetOrderStatusDoneEventData.builder()
                .orderId(orderId)
                .customerId(customerId)
                .event(Event.SET_ORDER_STATUS_DONE)
                .build();

        // WHEN
        kafkaProducer.sendEvent(kafkaProducer.getOrderCommandTopic(), customerId.toString(), data);

        // THEN
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(2, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(orderServiceMock).setStatusDone(orderId));
    }

    @Test
    @SneakyThrows
    @DisplayName("Call deleteById method of OrderService by kafka request")
    void deleteOrderByIdByKafkaRequest() {
        // GIVEN
        final UUID orderId = UUID.randomUUID();
        final Long customerId = 1L;

        final DeleteOrderEventData data = DeleteOrderEventData.builder()
                .orderId(orderId)
                .customerId(customerId)
                .event(Event.DELETE_ORDER)
                .build();

        // WHEN
        kafkaProducer.sendEvent(kafkaProducer.getOrderCommandTopic(), customerId.toString(), data);

        // THEN
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(2, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(orderServiceMock).deleteById(orderId));
    }
}
