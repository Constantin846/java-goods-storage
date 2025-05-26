package tk.project.goodsstorage.services.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import tk.project.goodsstorage.dto.CurrenciesDto;
import tk.project.goodsstorage.dto.product.ProductDto;
import tk.project.goodsstorage.enums.Currency;
import tk.project.goodsstorage.headerfilter.SessionCurrencyWrapper;
import tk.project.goodsstorage.mappers.ProductDtoMapperImpl;
import tk.project.goodsstorage.models.product.Product;
import tk.project.goodsstorage.models.product.objectmother.ProductMother;
import tk.project.goodsstorage.repositories.product.ProductRepository;
import tk.project.goodsstorage.repositories.product.ProductSpecification;
import tk.project.goodsstorage.search.criteria.BigDecimalCriteria;
import tk.project.goodsstorage.search.criteria.InstantCriteria;
import tk.project.goodsstorage.search.criteria.LocalDateCriteria;
import tk.project.goodsstorage.search.criteria.LongCriteria;
import tk.project.goodsstorage.search.criteria.SearchCriteria;
import tk.project.goodsstorage.search.criteria.StringCriteria;
import tk.project.goodsstorage.search.enums.Operation;
import tk.project.goodsstorage.services.currency.CurrencyServiceImpl;
import tk.project.goodsstorage.services.currency.provider.CurrenciesProvider;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:gs;LOCK_TIMEOUT=60000",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.liquibase.enabled=false"
})
@Import(value = {CurrencyServiceImpl.class, ProductDtoMapperImpl.class,
        ProductServiceImpl.class, ProductSpecification.class})
class ProductServiceImplDataJpaTest {
    @MockBean
    private CurrenciesProvider currenciesProvider;
    @MockBean
    private SessionCurrencyWrapper sessionCurrencyWrapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductServiceImpl productServiceUnderTest;
    private Product product;

    @BeforeEach
    void saveProduct() {
        product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);
    }

    @Test
    void findByCriteria() {
        when(sessionCurrencyWrapper.getCurrency()).thenReturn(Currency.RUS);
        when(currenciesProvider.getCurrencies()).thenReturn(new CurrenciesDto(1.1, 1.2, 1.3));

        List<SearchCriteria> searchCriteria = createCriteria();

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

    private List<SearchCriteria> createCriteria() {
        List<SearchCriteria> criteria = new ArrayList<>();
        StringCriteria nameCriteria = StringCriteria.builder()
                .field("name")
                .value(product.getName())
                .operation(Operation.EQUALS)
                .build();
        criteria.add(nameCriteria);

        StringCriteria articleCriteria = StringCriteria.builder()
                .field("article")
                .value(product.getArticle())
                .operation(Operation.MORE_OR_EQUALS)
                .build();
        criteria.add(articleCriteria);

        StringCriteria descriptionCriteria = StringCriteria.builder()
                .field("description")
                .value(product.getDescription().substring(4))
                .operation(Operation.LESS_OR_EQUALS)
                .build();
        criteria.add(descriptionCriteria);

        BigDecimalCriteria priceCriteria = BigDecimalCriteria.builder()
                .field("price")
                .value(product.getPrice())
                .operation(Operation.LIKE)
                .build();
        criteria.add(priceCriteria);

        LongCriteria countCriteria = LongCriteria.builder()
                .field("count")
                .value(product.getCount())
                .operation(Operation.LIKE)
                .build();
        criteria.add(countCriteria);

        InstantCriteria lastCountUpdateTimeCriteria = InstantCriteria.builder()
                .field("lastCountUpdateTime")
                .value(product.getLastCountUpdateTime())
                .operation(Operation.EQUALS)
                .build();
        criteria.add(lastCountUpdateTimeCriteria);

        LocalDateCriteria createDateCriteria = LocalDateCriteria.builder()
                .field("createDate")
                .value(product.getCreateDate())
                .operation(Operation.EQUALS)
                .build();
        criteria.add(createDateCriteria);

        return criteria;
    }
}