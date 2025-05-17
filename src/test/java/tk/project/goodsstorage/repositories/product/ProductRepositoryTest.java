package tk.project.goodsstorage.repositories.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.enums.CategoryType;
import tk.project.goodsstorage.models.product.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:gs;LOCK_TIMEOUT=60000",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.liquibase.enabled=false"
})
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepositoryUnderTest;
    private Product product;
    private final String name = "product";
    private final String article = "article";
    private final BigDecimal price = BigDecimal.valueOf(1000);
    private final Long count = 100L;
    private final Instant lastCountUpdateTime = Instant.now();
    private final LocalDate createDate = LocalDate.now();

    @BeforeEach
    void saveProduct() {
        product = Product.builder()
                .name(name)
                .article(article)
                .description("description")
                .category(CategoryType.FRUIT)
                .price(price)
                .count(count)
                .lastCountUpdateTime(lastCountUpdateTime)
                .createDate(createDate)
                .isAvailable(true)
                .build();
        product = productRepositoryUnderTest.save(product);
    }

    @Test
    void searchNameEquals() {
        Specification<Product> specification =
                ProductSpecifications.hasProductStringFieldEquals("name", List.of(name));

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getName(), actualProduct.getName());
    }

    @Test
    void searchNameStartWith() {
        Specification<Product> specification =
                ProductSpecifications.hasProductStringFieldStartWith("name", name.substring(0, 3));

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getName(), actualProduct.getName());
    }

    @Test
    void searchNameEndWith() {
        Specification<Product> specification =
                ProductSpecifications.hasProductStringFieldEndWith("name", name.substring(3));

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getName(), actualProduct.getName());
    }

    @Test
    void searchNameLike() {
        Specification<Product> specification =
                ProductSpecifications.hasProductStringFieldLike("name", name.substring(2, 5));

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getName(), actualProduct.getName());
    }

    @Test
    void searchPriceEquals() {
        Specification<Product> specification =
                ProductSpecifications.hasProductNumberFieldEquals("price", List.of(price));

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getPrice(), actualProduct.getPrice());
    }

    @Test
    void searchPriceMore() {
        Specification<Product> specification =
                ProductSpecifications.hasProductPriceMoreOrEquals(price.subtract(BigDecimal.ONE));

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getPrice(), actualProduct.getPrice());
    }

    @Test
    void searchPriceLess() {
        Specification<Product> specification =
                ProductSpecifications.hasProductPriceLessOrEquals(price.add(BigDecimal.ONE));

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getPrice(), actualProduct.getPrice());
    }

    @Test
    void searchCountMore() {
        Specification<Product> specification =
                ProductSpecifications.hasProductCountMoreOrEquals(count - 1);

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getCount(), actualProduct.getCount());
    }

    @Test
    void searchCountLess() {
        Specification<Product> specification =
                ProductSpecifications.hasProductCountLessOrEquals(count + 1);

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getCount(), actualProduct.getCount());
    }

    @Test
    void searchLastCountUpdateTimeBefore() {
        Specification<Product> specification = ProductSpecifications
                .hasProductLastCountUpdateTimeBefore(lastCountUpdateTime.plusSeconds(10L));

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getLastCountUpdateTime(), actualProduct.getLastCountUpdateTime());
    }

    @Test
    void searchLastCountUpdateTimeAfter() {
        Specification<Product> specification = ProductSpecifications
                .hasProductLastCountUpdateTimeAfter(lastCountUpdateTime.minusSeconds(10L));

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getLastCountUpdateTime(), actualProduct.getLastCountUpdateTime());
    }

    @Test
    void searchCreateDateEquals() {
        Specification<Product> specification = ProductSpecifications
                .hasProductCreateDateEquals(createDate);

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getCreateDate(), actualProduct.getCreateDate());
    }

    @Test
    void searchCreateDateBefore() {
        Specification<Product> specification = ProductSpecifications
                .hasProductCreateDateBefore(createDate.plusDays(10L));

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getCreateDate(), actualProduct.getCreateDate());
    }

    @Test
    void searchCreateDateAfter() {
        Specification<Product> specification = ProductSpecifications
                .hasProductCreateDateAfter(createDate.minusDays(10L));

        Product actualProduct = productRepositoryUnderTest.findAll(specification).get(0);

        assertEquals(product, actualProduct);
        assertEquals(product.getCreateDate(), actualProduct.getCreateDate());
    }

    @Test
    void findByArticle() {
        Product actualProduct = productRepositoryUnderTest.findByArticle(article).get();

        assertEquals(product, actualProduct);
    }

    @Test
    void findByIdLocked() {
        Product actualProduct = productRepositoryUnderTest.findByIdLocked(product.getId()).get();

        assertEquals(product, actualProduct);
    }

    @Test
    void findAll() {
        int expectedSize = 1;

        List<Product> productList = productRepositoryUnderTest.findAll();
        Product actualProduct = productList.get(0);

        assertEquals(expectedSize, productList.size());
        assertEquals(product, actualProduct);
    }
}