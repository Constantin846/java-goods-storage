package tk.project.goodsstorage.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tk.project.goodsstorage.order.dto.create.CreateOrderDto;
import tk.project.goodsstorage.order.dto.create.CreateOrderRequest;
import tk.project.goodsstorage.order.dto.find.FindOrderDto;
import tk.project.goodsstorage.order.dto.find.FindOrderResponse;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDtoRes;
import tk.project.goodsstorage.order.dto.update.UpdateOrderRequest;
import tk.project.goodsstorage.order.dto.update.UpdateOrderResponse;
import tk.project.goodsstorage.order.model.Order;

@Mapper(componentModel = "spring")
public interface OrderDtoMapper {
    OrderDtoMapper MAPPER = Mappers.getMapper(OrderDtoMapper.class);

    CreateOrderDto toCreateOrderDto(CreateOrderRequest orderRequest);

    Order toOrder(CreateOrderDto orderDto);

    FindOrderResponse toFindOrderResponse(FindOrderDto orderDto);

    UpdateOrderDto toUpdateOrderDto(UpdateOrderRequest orderRequest);

    UpdateOrderDtoRes toUpdateOrderDtoRes(Order order);

    UpdateOrderResponse toUpdateOrderResponse(UpdateOrderDtoRes orderDto);
}
