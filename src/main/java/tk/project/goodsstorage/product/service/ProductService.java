package tk.project.goodsstorage.product.service;

import org.springframework.data.domain.Pageable;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.create.CreateProductDto;
import tk.project.goodsstorage.product.dto.find.PageFindRequest;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.dto.update.UpdateProductDto;

import java.util.List;
import java.util.UUID;


public interface ProductService {
    UUID create(CreateProductDto createProductDto);

    List<ProductDto> findByCriteria(Pageable pageable, List<SearchCriteria<?>> criteria);

    ProductDto findById(UUID id);

    List<ProductDto> findAll(PageFindRequest pageRequest);

    UpdateProductDto update(UpdateProductDto productDto);

    void deleteById(UUID id);
}
