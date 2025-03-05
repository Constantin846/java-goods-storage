package tk.project.goodsstorage.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tk.project.goodsstorage.order.dto.create.CreateOrderProductDto;
import tk.project.goodsstorage.order.model.OrderProduct;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderProductDtoMapper {
    OrderProductDtoMapper MAPPER = Mappers.getMapper(OrderProductDtoMapper.class);

    @Mapping(target = "productId", source = "id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "price", ignore = true)
    OrderProduct toOrderProduct(CreateOrderProductDto orderProductDto);

    default List<OrderProduct> toOrderProduct(List<CreateOrderProductDto> orderProductsDto) {
        return orderProductsDto.stream()
                .map(this::toOrderProduct)
                .toList();
    }
}
