package tk.project.goodsstorage.services.product;

import org.springframework.data.domain.Pageable;
import tk.project.goodsstorage.dto.product.ProductDto;
import tk.project.goodsstorage.dto.product.create.CreateProductDto;
import tk.project.goodsstorage.dto.product.find.PageFindRequest;
import tk.project.goodsstorage.dto.product.update.UpdateProductDto;
import tk.project.goodsstorage.search.criteria.SearchCriteria;

import java.util.List;
import java.util.UUID;


public interface ProductService {
    UUID create(final CreateProductDto createProductDto);

    List<ProductDto> findByCriteria(final Pageable pageable, final List<SearchCriteria> criteria);

    ProductDto findById(final UUID id);

    List<ProductDto> findAll(final PageFindRequest pageRequest);

    UpdateProductDto update(final UpdateProductDto productDto, final UUID productId);

    void deleteById(final UUID id);
}
