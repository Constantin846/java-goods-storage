package tk.project.goodsstorage.product.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tk.project.goodsstorage.currency.Currency;
import tk.project.goodsstorage.currency.SessionCurrencyWrapper;
import tk.project.goodsstorage.currency.converter.CurrencyConverter;
import tk.project.goodsstorage.exceptions.product.ArticleExistsException;
import tk.project.goodsstorage.exceptions.product.ProductNotFoundException;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.create.CreateProductDto;
import tk.project.goodsstorage.product.dto.update.UpdateProductDto;
import tk.project.goodsstorage.product.mapper.ProductDtoMapper;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private CurrencyConverter currencyConverterMock;
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
        CreateProductDto createProductDto = new CreateProductDto();
        createProductDto.setName("name");
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
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        Currency currency = Currency.RUS;

        when(mapperMock.toProductDto(product)).thenReturn(productDto);
        when(productRepositoryMock.findById(id)).thenReturn(Optional.of(product));
        when(sessionCurrencyWrapperMock.getCurrency()).thenReturn(currency);
        when(currencyConverterMock.changeCurrency(productDto, currency)).thenReturn(productDto);

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
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setId(UUID.randomUUID());
        Product product = new Product();
        product.setId(updateProductDto.getId());

        when(mapperMock.toProduct(updateProductDto)).thenReturn(product);
        when(mapperMock.toUpdateProductDto(product)).thenReturn(updateProductDto);
        when(productRepositoryMock.findByIdLocked(updateProductDto.getId())).thenReturn(Optional.of(product));
        when(productRepositoryMock.save(product)).thenReturn(product);
        when(productRepositoryMock.findById(updateProductDto.getId())).thenReturn(Optional.of(product));

        UpdateProductDto updateProductDtoActual = productServiceUnderTest.update(updateProductDto);

        assertEquals(updateProductDto.getId(), updateProductDtoActual.getId());
    }

    @Test
    void update_articleExistsException() {
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setId(UUID.randomUUID());
        Product product = new Product();
        product.setId(updateProductDto.getId());
        Product oldProduct = new Product();
        oldProduct.setId(updateProductDto.getId());
        String article = "article";
        updateProductDto.setArticle(article);
        product.setArticle(article);

        when(mapperMock.toProduct(updateProductDto)).thenReturn(product);
        when(productRepositoryMock.findByIdLocked(updateProductDto.getId())).thenReturn(Optional.of(oldProduct));
        when(productRepositoryMock.findByArticle(product.getArticle())).thenReturn(Optional.of(new Product()));

        assertThrows(ArticleExistsException.class, () -> productServiceUnderTest.update(updateProductDto));
    }

    @Test
    void update_productNotFoundException() {
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setId(UUID.randomUUID());

        when(productRepositoryMock.findByIdLocked(updateProductDto.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productServiceUnderTest.update(updateProductDto));
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