package tk.project.goodsstorage.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import tk.project.goodsstorage.product.dto.CreateProductDto;
import tk.project.goodsstorage.product.dto.CreateProductRequest;
import tk.project.goodsstorage.product.dto.PageFindRequest;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.ProductResponse;
import tk.project.goodsstorage.product.dto.UpdateProductDto;
import tk.project.goodsstorage.product.dto.UpdateProductRequest;
import tk.project.goodsstorage.product.dto.UpdateProductResponse;
import tk.project.goodsstorage.product.mapper.ProductDtoMapper;
import tk.project.goodsstorage.product.service.ProductService;

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
    public Map<String, UUID> create(@Valid @RequestBody CreateProductRequest productRequest) {
        log.info("Create product: {}", productRequest);
        CreateProductDto productDto = mapper.toCreateProductDto(productRequest);
        return Map.of(ID, productService.create(productDto));
    }

    @GetMapping(ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse findById(@PathVariable(ID) UUID id) {
        log.info("Find product by id={}", id);
        ProductDto productDto = productService.findById(id);
        return mapper.toProductResponse(productDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> findAll(@Valid PageFindRequest pageRequest) {
        log.info("Find all products with page={}", pageRequest);
        List<ProductDto> products = productService.findAll(pageRequest);
        return mapper.toProductResponse(products);
    }

    @PatchMapping(ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public UpdateProductResponse updateById(@Valid @RequestBody UpdateProductRequest productRequest,
                                            @PathVariable(ID) UUID id) {
        log.info("Update product: {}, by id={}", productRequest, id);
        UpdateProductDto productDto = mapper.toUpdateProductDto(productRequest);
        productDto.setId(id);
        productDto = productService.update(productDto);
        return mapper.toUpdateProductResponse(productDto);
    }

    @DeleteMapping(ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable(ID) UUID id) {
        log.info("Delete product by id={}", id);
        productService.deleteById(id);
    }
}
