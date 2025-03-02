package tk.project.goodsstorage.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import tk.project.goodsstorage.currency.CurrenciesDto;
import tk.project.goodsstorage.currency.Currency;
import tk.project.goodsstorage.currency.SessionCurrencyWrapper;
import tk.project.goodsstorage.currency.converter.CurrencyConverterImpl;
import tk.project.goodsstorage.currency.provider.CurrenciesProvider;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.find.criteria.BigDecimalCriteria;
import tk.project.goodsstorage.product.dto.find.criteria.InstantCriteria;
import tk.project.goodsstorage.product.dto.find.criteria.LocalDateCriteria;
import tk.project.goodsstorage.product.dto.find.criteria.LongCriteria;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.dto.find.criteria.StringCriteria;
import tk.project.goodsstorage.product.mapper.ProductDtoMapperImpl;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.model.objectmother.ProductMother;
import tk.project.goodsstorage.product.repository.ProductRepository;
import tk.project.goodsstorage.product.service.criteria.Operation;
import tk.project.goodsstorage.product.service.criteria.SearchCriteriaManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:gs;LOCK_TIMEOUT=60000",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false"
})
@Import(value = {CurrencyConverterImpl.class, ProductDtoMapperImpl.class,
        ProductServiceImpl.class, SearchCriteriaManager.class})
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
        when(currenciesProvider.getCurrencies()).thenReturn(CurrenciesDto.ofDoubles(1.1, 1.2, 1.3));

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
        nameCriteria.setValue(product.getName());
        nameCriteria.setOperation(Operation.EQUALS);
        criteria.add(nameCriteria);

        StringCriteria articleCriteria = new StringCriteria();
        articleCriteria.setField("article");
        articleCriteria.setValue(product.getArticle());
        articleCriteria.setOperation(Operation.MORE_OR_EQUALS);
        criteria.add(articleCriteria);

        StringCriteria descriptionCriteria = new StringCriteria();
        descriptionCriteria.setField("description");
        descriptionCriteria.setValue(product.getDescription().substring(4));
        descriptionCriteria.setOperation(Operation.LESS_OR_EQUALS);
        criteria.add(descriptionCriteria);

        BigDecimalCriteria priceCriteria = new BigDecimalCriteria();
        priceCriteria.setField("price");
        priceCriteria.setValue(product.getPrice());
        priceCriteria.setOperation(Operation.LIKE);
        criteria.add(priceCriteria);

        LongCriteria countCriteria = new LongCriteria();
        countCriteria.setField("count");
        countCriteria.setValue(product.getCount());
        countCriteria.setOperation(Operation.LIKE);
        criteria.add(countCriteria);

        InstantCriteria lastCountUpdateTimeCriteria = new InstantCriteria();
        lastCountUpdateTimeCriteria.setField("lastCountUpdateTime");
        lastCountUpdateTimeCriteria.setValue(product.getLastCountUpdateTime());
        lastCountUpdateTimeCriteria.setOperation(Operation.EQUALS);
        criteria.add(lastCountUpdateTimeCriteria);

        LocalDateCriteria createDateCriteria = new LocalDateCriteria();
        createDateCriteria.setField("createDate");
        createDateCriteria.setValue(product.getCreateDate());
        createDateCriteria.setOperation(Operation.EQUALS);
        criteria.add(createDateCriteria);

        return criteria;
    }
}