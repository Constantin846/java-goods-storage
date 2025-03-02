package tk.project.goodsstorage.product.service.criteria;

import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductSpecifications;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PriceCriteriaManager implements ProductFieldCriteriaManager<BigDecimal> {
    private static final BigDecimal INCREASE_PERCENTAGE = BigDecimal.valueOf(1.2);
    private static final BigDecimal DECREASE_PERCENTAGE = BigDecimal.valueOf(0.8);
    private static final Map<Operation, Function<SearchCriteria<BigDecimal>, Specification<Product>>> FUNCTIONS =
            Map.of(
                Operation.EQUALS, PriceCriteriaManager::searchEquals,
                Operation.MORE_OR_EQUALS, PriceCriteriaManager::searchMoreOrEquals,
                Operation.LESS_OR_EQUALS, PriceCriteriaManager::searchLessOrEquals,
                Operation.LIKE, PriceCriteriaManager::searchLike
            );

    @Override
    public Specification<Product> getSpecification(SearchCriteria<BigDecimal> criteria) {
        return FUNCTIONS.get(criteria.getOperation()).apply(criteria);
    }

    private static Specification<Product> searchEquals(SearchCriteria<BigDecimal> criteria) {
        return ProductSpecifications.hasProductNumberFieldEquals(criteria.getField(), List.of(criteria.getValue()));
    }

    private static Specification<Product> searchMoreOrEquals(SearchCriteria<BigDecimal> criteria) {
        return ProductSpecifications.hasProductPriceMoreOrEquals(criteria.getValue());
    }

    private static Specification<Product> searchLessOrEquals(SearchCriteria<BigDecimal> criteria) {
        return ProductSpecifications.hasProductPriceLessOrEquals(criteria.getValue());
    }

    private static Specification<Product> searchLike(SearchCriteria<BigDecimal> criteria) {
        Specification<Product> specification =
                ProductSpecifications.hasProductPriceMoreOrEquals(criteria.getValue().multiply(DECREASE_PERCENTAGE));
        return specification.and(
                ProductSpecifications.hasProductPriceLessOrEquals(criteria.getValue().multiply(INCREASE_PERCENTAGE)));
    }
}
