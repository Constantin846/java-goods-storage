package tk.project.goodsstorage.product.service.criteria;

import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.product.dto.find.criteria.Operation;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductSpecifications;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PriceCriteriaManager implements ProductFieldCriteriaManager<BigDecimal> {
    private static final BigDecimal INCREASE_PERCENTAGE = BigDecimal.valueOf(1.2);
    private static final BigDecimal DECREASE_PERCENTAGE = BigDecimal.valueOf(0.8);
    private final Map<Operation, Function<SearchCriteria<BigDecimal>, Specification<Product>>> functions;

    public PriceCriteriaManager() {
        functions = new HashMap<>();
        functions.put(Operation.EQUALS, this::searchPriceEquals);
        functions.put(Operation.MORE_OR_EQUALS, this::searchPriceMoreOrEquals);
        functions.put(Operation.LESS_OR_EQUALS, this::searchPriceLessOrEquals);
        functions.put(Operation.LIKE, this::searchPriceLike);
    }

    @Override
    public Specification<Product> getSpecification(SearchCriteria<BigDecimal> criteria) {
        return functions.get(criteria.getOperation()).apply(criteria);
    }

    private Specification<Product> searchPriceEquals(SearchCriteria<BigDecimal> criteria) {
        return ProductSpecifications.hasProductNumberFieldEquals(criteria.getField(), List.of(criteria.getValue()));
    }

    private Specification<Product> searchPriceMoreOrEquals(SearchCriteria<BigDecimal> criteria) {
        return ProductSpecifications.hasProductPriceMoreOrEquals(criteria.getValue());
    }

    private Specification<Product> searchPriceLessOrEquals(SearchCriteria<BigDecimal> criteria) {
        return ProductSpecifications.hasProductPriceLessOrEquals(criteria.getValue());
    }

    private Specification<Product> searchPriceLike(SearchCriteria<BigDecimal> criteria) {
        Specification<Product> specification =
                ProductSpecifications.hasProductPriceMoreOrEquals(criteria.getValue().multiply(DECREASE_PERCENTAGE));
        return specification.and(
                ProductSpecifications.hasProductPriceLessOrEquals(criteria.getValue().multiply(INCREASE_PERCENTAGE)));
    }
}
