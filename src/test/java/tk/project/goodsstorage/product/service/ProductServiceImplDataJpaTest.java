package tk.project.goodsstorage.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.find.criteria.BigDecimalCriteria;
import tk.project.goodsstorage.product.dto.find.criteria.InstantCriteria;
import tk.project.goodsstorage.product.dto.find.criteria.LocalDateCriteria;
import tk.project.goodsstorage.product.dto.find.criteria.LongCriteria;
import tk.project.goodsstorage.product.dto.find.criteria.Operation;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.dto.find.criteria.StringCriteria;
import tk.project.goodsstorage.product.mapper.ProductDtoMapperImpl;
import tk.project.goodsstorage.product.model.CategoryType;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductRepository;
import tk.project.goodsstorage.product.service.criteria.SearchCriteriaManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:gs;LOCK_TIMEOUT=60000",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false"
})
@Import(value = {ProductDtoMapperImpl.class, ProductServiceImpl.class, SearchCriteriaManager.class})
class ProductServiceImplDataJpaTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductServiceImpl productServiceUnderTest;
    private Product product;
    private final String name = "product";
    private final String article = "article";
    private final String description = "description text";
    private final BigDecimal price = BigDecimal.valueOf(1000);
    private final Long count = 100L;
    private final Instant lastCountUpdateTime = Instant.now();
    private final LocalDate createDate = LocalDate.now();

    @BeforeEach
    void saveProduct() {
        product = Product.builder()
                .name(name)
                .article(article)
                .description(description)
                .category(CategoryType.FRUIT)
                .price(price)
                .count(count)
                .lastCountUpdateTime(lastCountUpdateTime)
                .createDate(createDate)
                .build();
        product = productRepository.save(product);
    }

    @Test
    void findByCriteria() {
        List<SearchCriteria<?>> searchCriteria = createCriteria();

        List<ProductDto> result = productServiceUnderTest
                .findByCriteria(PageRequest.of(0, 10), searchCriteria);
        ProductDto actualProduct = result.get(0);

        assertEquals(product.getId(), actualProduct.getId());
        assertEquals(product.getName(), actualProduct.getName());
        assertEquals(product.getArticle(), actualProduct.getArticle());
        assertEquals(product.getDescription(), actualProduct.getDescription());
        assertEquals(product.getCategory().toString(), actualProduct.getCategory());
        assertEquals(product.getPrice(), actualProduct.getPrice());
        assertEquals(product.getCount(), actualProduct.getCount());
        assertEquals(product.getLastCountUpdateTime(), actualProduct.getLastCountUpdateTime());
        assertEquals(product.getCreateDate(), actualProduct.getCreateDate());
    }

    private List<SearchCriteria<?>> createCriteria() {
        List<SearchCriteria<?>> criteria = new ArrayList<>();
        StringCriteria nameCriteria = new StringCriteria();
        nameCriteria.setField("name");
        nameCriteria.setValue(name);
        nameCriteria.setOperation(Operation.EQUALS);
        criteria.add(nameCriteria);

        StringCriteria articleCriteria = new StringCriteria();
        articleCriteria.setField("article");
        articleCriteria.setValue(article);
        articleCriteria.setOperation(Operation.MORE_OR_EQUALS);
        criteria.add(articleCriteria);

        StringCriteria descriptionCriteria = new StringCriteria();
        descriptionCriteria.setField("description");
        descriptionCriteria.setValue("text");
        descriptionCriteria.setOperation(Operation.LESS_OR_EQUALS);
        criteria.add(descriptionCriteria);

        BigDecimalCriteria priceCriteria = new BigDecimalCriteria();
        priceCriteria.setField("price");
        priceCriteria.setValue(price);
        priceCriteria.setOperation(Operation.LIKE);
        criteria.add(priceCriteria);

        LongCriteria countCriteria = new LongCriteria();
        countCriteria.setField("count");
        countCriteria.setValue(count);
        countCriteria.setOperation(Operation.LIKE);
        criteria.add(countCriteria);

        InstantCriteria lastCountUpdateTimeCriteria = new InstantCriteria();
        lastCountUpdateTimeCriteria.setField("lastCountUpdateTime");
        lastCountUpdateTimeCriteria.setValue(lastCountUpdateTime);
        lastCountUpdateTimeCriteria.setOperation(Operation.EQUALS);
        criteria.add(lastCountUpdateTimeCriteria);

        LocalDateCriteria createDateCriteria = new LocalDateCriteria();
        createDateCriteria.setField("createDate");
        createDateCriteria.setValue(createDate);
        createDateCriteria.setOperation(Operation.EQUALS);
        criteria.add(createDateCriteria);

        return criteria;
    }
}