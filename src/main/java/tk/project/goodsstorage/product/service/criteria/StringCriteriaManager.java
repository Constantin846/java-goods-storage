package tk.project.goodsstorage.product.service.criteria;

import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductSpecifications;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class StringCriteriaManager implements ProductFieldCriteriaManager<String> {
    private static final Map<Operation, Function<SearchCriteria<String>, Specification<Product>>> FUNCTIONS = Map.of(
            Operation.EQUALS, StringCriteriaManager::searchEquals,
            Operation.MORE_OR_EQUALS, StringCriteriaManager::searchStartWith,
            Operation.LESS_OR_EQUALS, StringCriteriaManager::searchEndWith,
            Operation.LIKE, StringCriteriaManager::searchLike
    );

    @Override
    public Specification<Product> getSpecification(SearchCriteria<String> criteria) {
        return FUNCTIONS.get(criteria.getOperation()).apply(criteria);
    }

    private static Specification<Product> searchEquals(SearchCriteria<String> criteria) {
        return ProductSpecifications.hasProductStringFieldEquals(criteria.getField(), List.of(criteria.getValue()));
    }

    private static Specification<Product> searchStartWith(SearchCriteria<String> criteria) {
        return ProductSpecifications.hasProductStringFieldStartWith(criteria.getField(), criteria.getValue());
    }

    private static Specification<Product> searchEndWith(SearchCriteria<String> criteria) {
        return ProductSpecifications.hasProductStringFieldEndWith(criteria.getField(), criteria.getValue());
    }

    private static Specification<Product> searchLike(SearchCriteria<String> criteria) {
        return ProductSpecifications.hasProductStringFieldLike(criteria.getField(), criteria.getValue());
    }
}
