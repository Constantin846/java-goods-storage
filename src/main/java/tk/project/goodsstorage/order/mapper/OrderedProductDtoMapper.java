package tk.project.goodsstorage.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tk.project.goodsstorage.order.dto.create.CreateOrderedProductDto;
import tk.project.goodsstorage.order.model.OrderedProduct;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderedProductDtoMapper {
    OrderedProductDtoMapper MAPPER = Mappers.getMapper(OrderedProductDtoMapper.class);

    @Mapping(target = "productId", source = "id")
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "price", ignore = true)
    OrderedProduct toOrderedProduct(CreateOrderedProductDto orderedProductDto);

    default List<OrderedProduct> toOrderedProduct(List<CreateOrderedProductDto> orderedProductsDto) {
        return orderedProductsDto.stream()
                .map(this::toOrderedProduct)
                .toList();
    }
}
