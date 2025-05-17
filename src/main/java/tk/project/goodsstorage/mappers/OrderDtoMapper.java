package tk.project.goodsstorage.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tk.project.goodsstorage.dto.order.create.CreateOrderDto;
import tk.project.goodsstorage.dto.order.create.CreateOrderRequest;
import tk.project.goodsstorage.dto.order.find.FindOrderDto;
import tk.project.goodsstorage.dto.order.find.FindOrderResponse;
import tk.project.goodsstorage.dto.order.update.UpdateOrderDto;
import tk.project.goodsstorage.dto.order.update.UpdateOrderDtoRes;
import tk.project.goodsstorage.dto.order.update.UpdateOrderRequest;
import tk.project.goodsstorage.dto.order.update.UpdateOrderResponse;
import tk.project.goodsstorage.models.order.Order;

@Mapper(componentModel = "spring")
public interface OrderDtoMapper {
    OrderDtoMapper MAPPER = Mappers.getMapper(OrderDtoMapper.class);

    CreateOrderDto toCreateOrderDto(final CreateOrderRequest orderRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "businessKey", ignore = true)
    @Mapping(target = "deliveryDate", ignore = true)
    Order toOrder(final CreateOrderDto orderDto);

    FindOrderResponse toFindOrderResponse(final FindOrderDto orderDto);

    @Mapping(target = "id", ignore = true)
    UpdateOrderDto toUpdateOrderDto(final UpdateOrderRequest orderRequest);

    UpdateOrderDtoRes toUpdateOrderDtoRes(final Order order);

    UpdateOrderResponse toUpdateOrderResponse(final UpdateOrderDtoRes orderDto);
}
