package tk.project.goodsstorage.services.order;

import tk.project.goodsstorage.dto.order.create.CreateOrderDto;
import tk.project.goodsstorage.dto.order.find.FindOrderDto;
import tk.project.goodsstorage.dto.order.update.SetOrderStatusRequest;
import tk.project.goodsstorage.dto.order.update.UpdateOrderDto;
import tk.project.goodsstorage.dto.order.update.UpdateOrderDtoRes;
import tk.project.goodsstorage.dto.order.update.UpdateOrderStatusDto;

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
