package tk.project.goodsstorage.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    CreateOrderDto toCreateOrderDto(final CreateOrderRequest orderRequest);

    @Mapping(target = "products", ignore = true)
    Order toOrder(final CreateOrderDto orderDto);

    FindOrderResponse toFindOrderResponse(final FindOrderDto orderDto);

    UpdateOrderDto toUpdateOrderDto(final UpdateOrderRequest orderRequest);

    UpdateOrderDtoRes toUpdateOrderDtoRes(final Order order);

    UpdateOrderResponse toUpdateOrderResponse(final UpdateOrderDtoRes orderDto);
}
