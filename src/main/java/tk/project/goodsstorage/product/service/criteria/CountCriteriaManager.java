package tk.project.goodsstorage.product.service.criteria;

import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductSpecifications;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CountCriteriaManager implements ProductFieldCriteriaManager<Long> {
    private static final Long INEQUALITY = 2L;
    private static final Map<Operation, Function<SearchCriteria<Long>, Specification<Product>>> FUNCTIONS = Map.of(
            Operation.EQUALS, CountCriteriaManager::searchEquals,
            Operation.MORE_OR_EQUALS, CountCriteriaManager::searchMoreOrEquals,
            Operation.LESS_OR_EQUALS, CountCriteriaManager::searchLessOrEquals,
            Operation.LIKE, CountCriteriaManager::searchLike
    );

    @Override
    public Specification<Product> getSpecification(SearchCriteria<Long> criteria) {
        return FUNCTIONS.get(criteria.getOperation()).apply(criteria);
    }

    private static Specification<Product> searchEquals(SearchCriteria<Long> criteria) {
        return ProductSpecifications.hasProductNumberFieldEquals(criteria.getField(), List.of(criteria.getValue()));
    }

    private static Specification<Product> searchMoreOrEquals(SearchCriteria<Long> criteria) {
        return ProductSpecifications.hasProductCountMoreOrEquals(criteria.getValue());
    }

    private static Specification<Product> searchLessOrEquals(SearchCriteria<Long> criteria) {
        return ProductSpecifications.hasProductCountLessOrEquals(criteria.getValue());
    }

    private static Specification<Product> searchLike(SearchCriteria<Long> criteria) {
        Specification<Product> specification =
                ProductSpecifications.hasProductCountMoreOrEquals(criteria.getValue() / INEQUALITY);
        return specification.and(
                ProductSpecifications.hasProductCountLessOrEquals(criteria.getValue() * INEQUALITY));
    }
}
