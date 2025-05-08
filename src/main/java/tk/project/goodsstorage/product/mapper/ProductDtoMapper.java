package tk.project.goodsstorage.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.create.CreateProductDto;
import tk.project.goodsstorage.product.dto.create.CreateProductRequest;
import tk.project.goodsstorage.product.dto.find.ProductResponse;
import tk.project.goodsstorage.product.dto.update.UpdateProductDto;
import tk.project.goodsstorage.product.dto.update.UpdateProductRequest;
import tk.project.goodsstorage.product.dto.update.UpdateProductResponse;
import tk.project.goodsstorage.product.model.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {
    ProductDtoMapper MAPPER = Mappers.getMapper(ProductDtoMapper.class);

    CreateProductDto toCreateProductDto(final CreateProductRequest createProductRequest);

    ProductResponse toProductResponse(final ProductDto productDto);

    default List<ProductResponse> toProductResponse(final List<ProductDto> productDtoList) {
        return productDtoList.stream().map(this::toProductResponse).toList();
    }

    UpdateProductDto toUpdateProductDto(final UpdateProductRequest updateProductRequest);

    UpdateProductResponse toUpdateProductResponse(final UpdateProductDto updateProductDto);

    Product toProduct(final CreateProductDto createProductDto);

    ProductDto toProductDto(final Product product);

    default List<ProductDto> toProductDto(final List<Product> products) {
        return products.stream().map(this::toProductDto).toList();
    }

    Product toProduct(final UpdateProductDto updateProductDto);

    UpdateProductDto toUpdateProductDto(final Product product);
}
