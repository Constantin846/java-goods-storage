package tk.project.goodsstorage.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tk.project.goodsstorage.product.dto.CreateProductDto;
import tk.project.goodsstorage.product.dto.CreateProductRequest;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.ProductResponse;
import tk.project.goodsstorage.product.dto.UpdateProductDto;
import tk.project.goodsstorage.product.dto.UpdateProductRequest;
import tk.project.goodsstorage.product.dto.UpdateProductResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {
    ProductDtoMapper MAPPER = Mappers.getMapper(ProductDtoMapper.class);

    CreateProductDto toCreateProductDto(CreateProductRequest createProductRequest);

    ProductResponse toProductResponse(ProductDto productDto);

    default List<ProductResponse> toProductResponse(List<ProductDto> productDtoList) {
        return productDtoList.stream()
                .map(this::toProductResponse)
                .toList();
    }

    UpdateProductDto toUpdateProductDto(UpdateProductRequest updateProductRequest);

    UpdateProductResponse toUpdateProductResponse(UpdateProductDto updateProductDto);
}
