package tk.project.goodsstorage.product.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.exceptions.ArticleExistsException;
import tk.project.goodsstorage.exceptions.ProductNotFoundException;
import tk.project.goodsstorage.product.Product;
import tk.project.goodsstorage.product.dto.CreateProductDto;
import tk.project.goodsstorage.product.dto.PageFindRequest;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.UpdateProductDto;
import tk.project.goodsstorage.product.mapper.ProductDtoMapper;
import tk.project.goodsstorage.product.repository.ProductRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductDtoMapper mapper;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public UUID create(CreateProductDto createProductDto) {
        throwExceptionIfArticleExists(createProductDto.getArticle());
        Product product = mapper.toProduct(createProductDto);
        product.setCreateDate(LocalDate.now());
        product.setLastCountUpdateTime(Instant.now());
        product = productRepository.save(product);
        return product.getId();
    }

    @Override
    public ProductDto findById(UUID id) {
        Product product = getById(id);
        return mapper.toProductDto(product);
    }

    @Override
    public List<ProductDto> findAll(PageFindRequest page) {
        List<Product> products =
                productRepository.findAll(PageRequest.of(page.getFrom(), page.getSize())).stream().toList();
        return mapper.toProductDto(products);
    }

    @Transactional
    @Override
    public UpdateProductDto update(UpdateProductDto productDto) {
        Product oldProduct = getById(productDto.getId());
        Product product = mapper.toProduct(productDto);
        oldProduct = updateFields(oldProduct, product);
        oldProduct = productRepository.save(oldProduct);
        return mapper.toUpdateProductDto(oldProduct);
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        if (Objects.nonNull(getById(id))) {
            productRepository.deleteById(id);
        }
    }

    private void throwExceptionIfArticleExists(String article) {
        if (Objects.nonNull(findByArticle(article))) {
            String message = String.format("Article has already existed: %s", article);
            log.warn(message);
            throw new ArticleExistsException(message);
        }
    }

    private Product findByArticle(String article) {
        return productRepository.findByArticle(article);
    }

    private Product getById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> {
            String message = String.format("Product was not found by id: %s", id);
            log.warn(message);
            return new ProductNotFoundException(message);
        });
    }

    private Product updateFields(Product oldProduct, Product newProduct) {
        if (Objects.nonNull(newProduct.getName())) {
            oldProduct.setName(newProduct.getName());
        }
        if (Objects.nonNull(newProduct.getArticle())) {
            if (!newProduct.getArticle().equals(oldProduct.getArticle())) {
                throwExceptionIfArticleExists(newProduct.getArticle());
                oldProduct.setArticle(newProduct.getArticle());
            }
        }
        if (Objects.nonNull(newProduct.getDescription())) {
            oldProduct.setDescription(newProduct.getDescription());
        }
        if (Objects.nonNull(newProduct.getCategory())) {
            oldProduct.setCategory(newProduct.getCategory());
        }
        if (Objects.nonNull(newProduct.getPrice())) {
            oldProduct.setPrice(newProduct.getPrice());
        }
        if (Objects.nonNull(newProduct.getCount())) {
            oldProduct.setCount(newProduct.getCount());
            oldProduct.setLastCountUpdateTime(Instant.now());
        }
        if (Objects.nonNull(newProduct.getCreateDate())) {
            oldProduct.setCreateDate(newProduct.getCreateDate());
        }
        return oldProduct;
    }
}
