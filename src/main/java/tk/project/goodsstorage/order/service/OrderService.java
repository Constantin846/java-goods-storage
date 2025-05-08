package tk.project.goodsstorage.order.service;

import tk.project.goodsstorage.order.dto.create.CreateOrderDto;
import tk.project.goodsstorage.order.dto.find.FindOrderDto;
import tk.project.goodsstorage.order.dto.update.SetOrderStatusRequest;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDtoRes;
import tk.project.goodsstorage.order.dto.update.UpdateOrderStatusDto;

import java.util.UUID;

public interface OrderService {
    UUID create(final CreateOrderDto orderDto);

    UUID confirmById(final UUID orderId);

    FindOrderDto findById(final UUID orderId);

    UpdateOrderDtoRes update(final UpdateOrderDto updateOrderDto, final UUID orderId);

    UpdateOrderStatusDto setStatusDone(final UUID orderId);

    UpdateOrderStatusDto setStatusByOrchestrator(final SetOrderStatusRequest statusRequest);

    void deleteById(final UUID orderId);
}
