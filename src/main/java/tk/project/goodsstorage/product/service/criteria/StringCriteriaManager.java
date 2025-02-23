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

public class StringCriteriaManager implements ItemCriteriaManager<String> {
    private final Map<Operation, Function<SearchCriteria<String>, Specification<Product>>> functions;

    public StringCriteriaManager() {
        functions = new HashMap<>();
        functions.put(Operation.EQUALS, this::searchStringEquals);
        functions.put(Operation.MORE_OR_EQUALS, this::searchStringStartWith);
        functions.put(Operation.LESS_OR_EQUALS, this::searchStringEndWith);
        functions.put(Operation.LIKE, this::searchStringLike);
    }

    @Override
    public Specification<Product> getSpecification(SearchCriteria<String> criteria) {
        return functions.get(criteria.getOperation()).apply(criteria);
    }

    private Specification<Product> searchStringEquals(SearchCriteria<String> criteria) {
        return ProductSpecifications.hasProductStringFieldEquals(criteria.getField(), List.of(criteria.getValue()));
    }

    private Specification<Product> searchStringStartWith(SearchCriteria<String> criteria) {
        return ProductSpecifications.hasProductStringFieldStartWith(criteria.getField(), criteria.getValue());
    }

    private Specification<Product> searchStringEndWith(SearchCriteria<String> criteria) {
        return ProductSpecifications.hasProductStringFieldEndWith(criteria.getField(), criteria.getValue());
    }

    private Specification<Product> searchStringLike(SearchCriteria<String> criteria) {
        return ProductSpecifications.hasProductStringFieldLike(criteria.getField(), criteria.getValue());
    }
}
