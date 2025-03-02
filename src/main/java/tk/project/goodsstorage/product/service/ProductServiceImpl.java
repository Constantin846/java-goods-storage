package tk.project.goodsstorage.product.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.currency.SessionCurrencyWrapper;
import tk.project.goodsstorage.currency.converter.CurrencyConverter;
import tk.project.goodsstorage.exceptions.product.ArticleExistsException;
import tk.project.goodsstorage.exceptions.product.ProductNotFoundException;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.create.CreateProductDto;
import tk.project.goodsstorage.product.dto.find.PageFindRequest;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.dto.update.UpdateProductDto;
import tk.project.goodsstorage.product.mapper.ProductDtoMapper;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductRepository;
import tk.project.goodsstorage.product.service.criteria.SearchCriteriaManager;

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
    private final SearchCriteriaManager searchCriteriaManager;
    private final SessionCurrencyWrapper sessionCurrencyWrapper;

    @Transactional
    @Override
    public UUID create(CreateProductDto createProductDto) {
        throwExceptionIfArticleExists(createProductDto.getArticle());
        Product product = mapper.toProduct(createProductDto);
        product = productRepository.save(product);
        return product.getId();
    }

    @Override
    public List<ProductDto> findByCriteria(Pageable pageable, List<SearchCriteria<?>> criteria) {
        List<Product> products;

        if (criteria.isEmpty()) {
            products = productRepository.findAll(pageable).stream().toList();

        } else {
            Specification<Product> specification = searchCriteriaManager.getSpecification(criteria);
            products = productRepository.findAll(specification, pageable).stream().toList();
        }
        return products.stream()
                .map(product -> {
                    ProductDto productDto = mapper.toProductDto(product);
                    return currencyConverter.changeCurrency(productDto, sessionCurrencyWrapper.getCurrency());
                }).toList();
    }

    @Override
    public ProductDto findById(UUID id) {
        Product product = getById(id);
        return currencyConverter.changeCurrency(mapper.toProductDto(product), sessionCurrencyWrapper.getCurrency());
    }

    @Override
    public List<ProductDto> findAll(PageFindRequest page) {
        List<Product> products =
                productRepository.findAll(PageRequest.of(page.getFrom(), page.getSize())).stream().toList();
        return products.stream()
                .map(product -> {
                    ProductDto productDto = mapper.toProductDto(product);
                    return currencyConverter.changeCurrency(productDto, sessionCurrencyWrapper.getCurrency());
                }).toList();
    }

    @Transactional
    @Override
    public UpdateProductDto update(UpdateProductDto productDto) {
        Product oldProduct = getByIdForUpdate(productDto.getId());
        Product product = mapper.toProduct(productDto);
        oldProduct = updateFields(oldProduct, product);
        oldProduct = productRepository.save(oldProduct);
        return mapper.toUpdateProductDto(getById(oldProduct.getId()));
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        if (Objects.nonNull(getById(id))) {
            productRepository.deleteById(id);
        }
    }

    private void throwExceptionIfArticleExists(String article) {
        Optional<Product> productOp = productRepository.findByArticle(article);
        if (productOp.isPresent()) {
            String message = String.format("Article has already existed: %s", article);
            log.warn(message);
            throw new ArticleExistsException(message, productOp.get().getId());
        }
    }

    private Product getById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> {
            String message = String.format(PRODUCT_WAS_NOT_FOUND_BY_ID, id);
            log.warn(message);
            return new ProductNotFoundException(message);
        });
    }

    private Product getByIdForUpdate(UUID id) {
        return productRepository.findByIdLocked(id).orElseThrow(() -> {
            String message = String.format(PRODUCT_WAS_NOT_FOUND_BY_ID, id);
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
        if (Objects.nonNull(newProduct.getIsAvailable())) {
            oldProduct.setIsAvailable(newProduct.getIsAvailable());
        }
        return oldProduct;
    }
}
