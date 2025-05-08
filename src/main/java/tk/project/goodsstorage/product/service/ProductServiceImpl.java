package tk.project.goodsstorage.product.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.ArticleExistsException;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.ProductNotFoundException;
import tk.project.goodsstorage.product.currency.SessionCurrencyWrapper;
import tk.project.goodsstorage.product.currency.converter.CurrencyConverter;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.create.CreateProductDto;
import tk.project.goodsstorage.product.dto.find.PageFindRequest;
import tk.project.goodsstorage.product.dto.update.UpdateProductDto;
import tk.project.goodsstorage.product.mapper.ProductDtoMapper;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductRepository;
import tk.project.goodsstorage.product.repository.ProductSpecification;
import tk.project.goodsstorage.search.criteria.SearchCriteria;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final String PRODUCT_WAS_NOT_FOUND_BY_ID = "Product was not found by id: %s";
    private final CurrencyConverter currencyConverter;
    private final ProductDtoMapper mapper;
    private final ProductRepository productRepository;
    private final SessionCurrencyWrapper sessionCurrencyWrapper;

    @Transactional
    @Override
    public UUID create(final CreateProductDto createProductDto) {
        throwExceptionIfArticleExists(createProductDto.getArticle());
        Product product = mapper.toProduct(createProductDto);
        product = productRepository.save(product);
        return product.getId();
    }

    @Override
    public List<ProductDto> findByCriteria(final Pageable pageable, final List<SearchCriteria> criteria) {
        List<Product> products;

        if (criteria.isEmpty()) {
            products = productRepository.findAll(pageable).stream().toList();

        } else {
            Specification<Product> specification = new ProductSpecification(criteria);
            products = productRepository.findAll(specification, pageable).stream().toList();
        }
        return products.stream()
                .map(product -> {
                    ProductDto productDto = mapper.toProductDto(product);
                    return currencyConverter.changeCurrency(productDto, sessionCurrencyWrapper.getCurrency());
                }).toList();
    }

    @Override
    public ProductDto findById(final UUID id) {
        final Product product = getById(id);
        return currencyConverter.changeCurrency(mapper.toProductDto(product), sessionCurrencyWrapper.getCurrency());
    }

    @Override
    public List<ProductDto> findAll(final PageFindRequest page) {
        final List<Product> products =
                productRepository.findAll(PageRequest.of(page.getFrom(), page.getSize())).stream().toList();
        return products.stream()
                .map(product -> {
                    ProductDto productDto = mapper.toProductDto(product);
                    return currencyConverter.changeCurrency(productDto, sessionCurrencyWrapper.getCurrency());
                }).toList();
    }

    @Transactional
    @Override
    public UpdateProductDto update(final UpdateProductDto productDto, final UUID productId) {
        Product oldProduct = getByIdForUpdate(productId);
        final Product product = mapper.toProduct(productDto);
        oldProduct = updateFieldsOldProduct(oldProduct, product);
        oldProduct = productRepository.save(oldProduct);
        return mapper.toUpdateProductDto(getById(oldProduct.getId()));
    }

    @Transactional
    @Override
    public void deleteById(final UUID id) {
        if (Objects.nonNull(getById(id))) {
            productRepository.deleteById(id);
        }
    }

    private void throwExceptionIfArticleExists(final String article) {
        final Optional<Product> productOp = productRepository.findByArticle(article);
        if (productOp.isPresent()) {
            String message = String.format("Article has already existed: %s", article);
            log.warn(message);
            throw new ArticleExistsException(message, productOp.get().getId());
        }
    }

    private Product getById(final UUID id) {
        return productRepository.findById(id).orElseThrow(() -> throwProductNotFoundException(id));
    }

    private Product getByIdForUpdate(final UUID id) {
        return productRepository.findByIdLocked(id).orElseThrow(() -> throwProductNotFoundException(id));
    }

    private ProductNotFoundException throwProductNotFoundException(final UUID id) {
        final String message = String.format(PRODUCT_WAS_NOT_FOUND_BY_ID, id);
        log.warn(message);
        return new ProductNotFoundException(message);
    }

    private Product updateFieldsOldProduct(Product oldProduct, final Product newProduct) {
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
        if (Objects.nonNull(newProduct.getIsAvailable())) {
            oldProduct.setIsAvailable(newProduct.getIsAvailable());
        }
        return oldProduct;
    }
}
