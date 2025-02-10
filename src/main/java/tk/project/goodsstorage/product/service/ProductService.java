package tk.project.goodsstorage.product.service;

import org.springframework.data.domain.PageRequest;
import tk.project.goodsstorage.product.dto.CreateProductDto;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.UpdateProductDto;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;


public interface ProductService {
    UUID create(CreateProductDto createProductDto);

    ProductDto findById(UUID id);

    List<ProductDto> findAll(PageRequest pageRequest);

    UpdateProductDto update(UpdateProductDto productDto);

    void deleteById(UUID id);
}
