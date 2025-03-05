package tk.project.goodsstorage.order.service;

import tk.project.goodsstorage.order.dto.create.CreateOrderDto;
import tk.project.goodsstorage.order.dto.find.FindOrderDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDtoRes;
import tk.project.goodsstorage.order.dto.update.UpdateOrderStatusDto;

import java.util.UUID;

public interface OrderService {
    UUID create(CreateOrderDto orderDto);

    void confirmById(UUID orderId, long customerId);

    FindOrderDto findById(UUID orderId, long customerId);

    UpdateOrderDtoRes update(UpdateOrderDto updateOrderDto);

    UpdateOrderStatusDto setStatusDone(UUID orderId, long customerId);

    void deleteById(UUID orderId, long customerId);
}
