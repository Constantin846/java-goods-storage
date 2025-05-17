package tk.project.goodsstorage.services.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tk.project.goodsstorage.dto.product.ProductDto;
import tk.project.goodsstorage.dto.product.create.CreateProductDto;
import tk.project.goodsstorage.dto.product.update.UpdateProductDto;
import tk.project.goodsstorage.enums.Currency;
import tk.project.goodsstorage.exceptionhandler.exceptions.product.ArticleExistsException;
import tk.project.goodsstorage.exceptionhandler.exceptions.product.ProductNotFoundException;
import tk.project.goodsstorage.headerfilter.SessionCurrencyWrapper;
import tk.project.goodsstorage.mappers.ProductDtoMapper;
import tk.project.goodsstorage.models.product.Product;
import tk.project.goodsstorage.repositories.product.ProductRepository;
import tk.project.goodsstorage.services.currency.CurrencyService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private CurrencyService currencyServiceMock;
    @Mock
    private ProductDtoMapper mapperMock;
    @Mock
    private ProductRepository productRepositoryMock;
    @Mock
    private SessionCurrencyWrapper sessionCurrencyWrapperMock;
    @InjectMocks
    private ProductServiceImpl productServiceUnderTest;

    @Test
    void create() {
        CreateProductDto createProductDto = CreateProductDto.builder().name("name").build();
        UUID idExpected = UUID.randomUUID();
        Product product = new Product();
        product.setId(idExpected);
        product.setName(createProductDto.getName());

        when(mapperMock.toProduct(createProductDto)).thenReturn(product);
        when(productRepositoryMock.save(product)).thenReturn(product);

        UUID idActual = productServiceUnderTest.create(createProductDto);

        assertEquals(idExpected, idActual);
    }

    @Test
    void findById() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);
        ProductDto productDto = ProductDto.builder().id(product.getId()).build();
        Currency currency = Currency.RUS;

        when(mapperMock.toProductDto(product)).thenReturn(productDto);
        when(productRepositoryMock.findById(id)).thenReturn(Optional.of(product));
        when(sessionCurrencyWrapperMock.getCurrency()).thenReturn(currency);
        when(currencyServiceMock.changeCurrency(productDto, currency)).thenReturn(productDto);

        ProductDto actualProductDto = productServiceUnderTest.findById(id);

        assertEquals(productDto.getId(), actualProductDto.getId());
    }

    @Test
    void findById_productNotFoundException() {
        UUID id = UUID.randomUUID();

        when(productRepositoryMock.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productServiceUnderTest.findById(id));
    }

    @Test
    void update() {
        UUID productId = UUID.randomUUID();
        UpdateProductDto updateProductDto = UpdateProductDto.builder().id(productId).build();
        Product product = new Product();
        product.setId(updateProductDto.getId());

        when(mapperMock.toProduct(updateProductDto)).thenReturn(product);
        when(mapperMock.toUpdateProductDto(product)).thenReturn(updateProductDto);
        when(productRepositoryMock.findByIdLocked(updateProductDto.getId())).thenReturn(Optional.of(product));
        when(productRepositoryMock.save(product)).thenReturn(product);
        when(productRepositoryMock.findById(updateProductDto.getId())).thenReturn(Optional.of(product));

        UpdateProductDto updateProductDtoActual = productServiceUnderTest.update(updateProductDto, productId);

        assertEquals(updateProductDto.getId(), updateProductDtoActual.getId());
    }

    @Test
    void update_articleExistsException() {
        UUID productId = UUID.randomUUID();
        String article = "article";
        UpdateProductDto updateProductDto = UpdateProductDto.builder().id(productId).article(article).build();
        Product product = new Product();
        product.setId(updateProductDto.getId());
        Product oldProduct = new Product();
        oldProduct.setId(updateProductDto.getId());
        product.setArticle(article);

        when(mapperMock.toProduct(updateProductDto)).thenReturn(product);
        when(productRepositoryMock.findByIdLocked(updateProductDto.getId())).thenReturn(Optional.of(oldProduct));
        when(productRepositoryMock.findByArticle(product.getArticle())).thenReturn(Optional.of(new Product()));

        assertThrows(ArticleExistsException.class, () -> productServiceUnderTest.update(updateProductDto, productId));
    }

    @Test
    void update_productNotFoundException() {
        UUID productId = UUID.randomUUID();
        UpdateProductDto updateProductDto = UpdateProductDto.builder().id(productId).build();

        when(productRepositoryMock.findByIdLocked(updateProductDto.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productServiceUnderTest.update(updateProductDto, productId));
    }

    @Test
    void deleteById() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);

        when(productRepositoryMock.findById(id)).thenReturn(Optional.of(product));

        productServiceUnderTest.deleteById(id);

        verify(productRepositoryMock).deleteById(id);
    }

    @Test
    void deleteById_productNotFoundException() {
        UUID id = UUID.randomUUID();

        when(productRepositoryMock.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productServiceUnderTest.deleteById(id));
    }
}