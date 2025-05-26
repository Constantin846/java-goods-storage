package tk.project.goodsstorage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tk.project.goodsstorage.dto.product.ProductDto;
import tk.project.goodsstorage.dto.product.create.CreateProductDto;
import tk.project.goodsstorage.dto.product.create.CreateProductRequest;
import tk.project.goodsstorage.dto.product.find.PageFindRequest;
import tk.project.goodsstorage.dto.product.find.ProductResponse;
import tk.project.goodsstorage.dto.product.update.UpdateProductDto;
import tk.project.goodsstorage.dto.product.update.UpdateProductRequest;
import tk.project.goodsstorage.dto.product.update.UpdateProductResponse;
import tk.project.goodsstorage.mappers.ProductDtoMapper;
import tk.project.goodsstorage.search.criteria.SearchCriteria;
import tk.project.goodsstorage.services.product.ProductService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private static final String ID = "id";
    private static final String ID_PATH = "/{id}";
    private final ProductDtoMapper mapper;
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, UUID> create(@Valid @RequestBody final CreateProductRequest productRequest) {
        log.info("Create product: {}", productRequest);
        CreateProductDto productDto = mapper.toCreateProductDto(productRequest);
        return Map.of(ID, productService.create(productDto));
    }

    @PostMapping("/search")
    public List<ProductResponse> findByCriteria(final Pageable pageable,
                                                @Valid @RequestBody final List<SearchCriteria> criteria) {
        log.info("Find product by criteria: {}", criteria);
        final List<ProductDto> products = productService.findByCriteria(pageable, criteria);
        return mapper.toProductResponse(products);
    }

    @GetMapping(ID_PATH)
    public ProductResponse findById(@PathVariable(ID) final UUID id) {
        log.info("Find product by id={}", id);
        final ProductDto productDto = productService.findById(id);
        return mapper.toProductResponse(productDto);
    }

    @GetMapping
    public List<ProductResponse> findAll(@Valid final PageFindRequest pageRequest) {
        log.info("Find all products with page={}", pageRequest);
        final List<ProductDto> products = productService.findAll(pageRequest);
        return mapper.toProductResponse(products);
    }

    @PatchMapping(ID_PATH)
    public UpdateProductResponse updateById(@Valid @RequestBody final UpdateProductRequest productRequest,
                                            @PathVariable(ID) final UUID id) {
        log.info("Update product: {}, by id={}", productRequest, id);
        final UpdateProductDto productDto = mapper.toUpdateProductDto(productRequest);
        final UpdateProductDto resultProductDto = productService.update(productDto, id);
        return mapper.toUpdateProductResponse(resultProductDto);
    }

    @DeleteMapping(ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable(ID) final UUID id) {
        log.info("Delete product by id={}", id);
        productService.deleteById(id);
    }
}
