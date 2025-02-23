package tk.project.goodsstorage.product.service.criteria;

import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.product.dto.find.criteria.Operation;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductSpecifications;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CountCriteriaManager implements ItemCriteriaManager<Long> {
    private static final Long INEQUALITY = 2L;
    private final Map<Operation, Function<SearchCriteria<Long>, Specification<Product>>> functions;

    public CountCriteriaManager() {
        functions = new HashMap<>();
        functions.put(Operation.EQUALS, this::searchCountEquals);
        functions.put(Operation.MORE_OR_EQUALS, this::searchCountMoreOrEquals);
        functions.put(Operation.LESS_OR_EQUALS, this::searchCountLessOrEquals);
        functions.put(Operation.LIKE, this::searchCountLike);
    }

    @Override
    public Specification<Product> getSpecification(SearchCriteria<Long> criteria) {
        return functions.get(criteria.getOperation()).apply(criteria);
    }

    private Specification<Product> searchCountEquals(SearchCriteria<Long> criteria) {
        return ProductSpecifications.hasProductNumberFieldEquals(criteria.getField(), List.of(criteria.getValue()));
    }

    private Specification<Product> searchCountMoreOrEquals(SearchCriteria<Long> criteria) {
        return ProductSpecifications.hasProductCountMoreOrEquals(criteria.getValue());
    }

    private Specification<Product> searchCountLessOrEquals(SearchCriteria<Long> criteria) {
        return ProductSpecifications.hasProductCountLessOrEquals(criteria.getValue());
    }

    private Specification<Product> searchCountLike(SearchCriteria<Long> criteria) {
        Specification<Product> specification =
                ProductSpecifications.hasProductCountMoreOrEquals(criteria.getValue() / INEQUALITY);
        return specification.and(
                ProductSpecifications.hasProductCountLessOrEquals(criteria.getValue() * INEQUALITY));
    }
}
