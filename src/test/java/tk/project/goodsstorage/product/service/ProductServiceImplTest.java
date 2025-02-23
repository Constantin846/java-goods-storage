package tk.project.goodsstorage.product.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tk.project.goodsstorage.exceptions.ArticleExistsException;
import tk.project.goodsstorage.exceptions.ProductNotFoundException;
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
    private ProductDtoMapper mapper;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void create() {
        CreateProductDto createProductDto = new CreateProductDto();
        createProductDto.setName("name");
        UUID idExpected = UUID.randomUUID();
        Product product = new Product();
        product.setId(idExpected);
        product.setName(createProductDto.getName());

        when(mapper.toProduct(createProductDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        UUID idActual = productService.create(createProductDto);

        assertEquals(idExpected, idActual);
    }

    @Test
    void findById() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());

        when(mapper.toProductDto(product)).thenReturn(productDto);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        ProductDto actualProductDto = productService.findById(id);

        assertEquals(productDto.getId(), actualProductDto.getId());
    }

    @Test
    void findById_productNotFoundException() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(id));
    }

    @Test
    void update() {
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setId(UUID.randomUUID());
        Product product = new Product();
        product.setId(updateProductDto.getId());

        when(mapper.toProduct(updateProductDto)).thenReturn(product);
        when(mapper.toUpdateProductDto(product)).thenReturn(updateProductDto);
        when(productRepository.findByIdLocked(updateProductDto.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productRepository.findById(updateProductDto.getId())).thenReturn(Optional.of(product));

        UpdateProductDto updateProductDtoActual = productService.update(updateProductDto);

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

        when(mapper.toProduct(updateProductDto)).thenReturn(product);
        when(productRepository.findByIdLocked(updateProductDto.getId())).thenReturn(Optional.of(oldProduct));
        when(productRepository.findByArticle(product.getArticle())).thenReturn(Optional.of(new Product()));

        assertThrows(ArticleExistsException.class, () -> productService.update(updateProductDto));
    }

    @Test
    void update_productNotFoundException() {
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setId(UUID.randomUUID());

        when(productRepository.findByIdLocked(updateProductDto.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.update(updateProductDto));
    }

    @Test
    void deleteById() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        productService.deleteById(id);

        verify(productRepository).deleteById(id);
    }

    @Test
    void deleteById_productNotFoundException() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteById(id));
    }
}