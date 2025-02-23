package tk.project.goodsstorage.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.ProductResponse;
import tk.project.goodsstorage.product.dto.create.CreateProductDto;
import tk.project.goodsstorage.product.dto.create.CreateProductRequest;
import tk.project.goodsstorage.product.dto.update.UpdateProductDto;
import tk.project.goodsstorage.product.dto.update.UpdateProductRequest;
import tk.project.goodsstorage.product.dto.update.UpdateProductResponse;
import tk.project.goodsstorage.product.model.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {
    ProductDtoMapper MAPPER = Mappers.getMapper(ProductDtoMapper.class);

    CreateProductDto toCreateProductDto(CreateProductRequest createProductRequest);

    ProductResponse toProductResponse(ProductDto productDto);

    default List<ProductResponse> toProductResponse(List<ProductDto> productDtoList) {
        return productDtoList.stream().map(this::toProductResponse).toList();
    }

    UpdateProductDto toUpdateProductDto(UpdateProductRequest updateProductRequest);

    UpdateProductResponse toUpdateProductResponse(UpdateProductDto updateProductDto);

    Product toProduct(CreateProductDto createProductDto);

    ProductDto toProductDto(Product product);

    default List<ProductDto> toProductDto(List<Product> products) {
        return products.stream().map(this::toProductDto).toList();
    }

    Product toProduct(UpdateProductDto updateProductDto);

    UpdateProductDto toUpdateProductDto(Product product);
}
