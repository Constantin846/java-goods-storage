package tk.project.goodsstorage.product.service.criteria;

import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.product.dto.find.criteria.Operation;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductSpecifications;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CreateDateCriteriaManager implements ProductFieldCriteriaManager<LocalDate> {
    private static final Long DELTA_DAYS_FOR_LIKE = 3L;
    private final Map<Operation, Function<SearchCriteria<LocalDate>, Specification<Product>>> functions;

    public CreateDateCriteriaManager() {
        functions = new HashMap<>();
        functions.put(Operation.EQUALS, this::searchEquals);
        functions.put(Operation.MORE_OR_EQUALS, this::searchMoreOrEquals);
        functions.put(Operation.LESS_OR_EQUALS, this::searchLessOrEquals);
        functions.put(Operation.LIKE, this::searchLike);
    }

    @Override
    public Specification<Product> getSpecification(SearchCriteria<LocalDate> criteria) {
        return functions.get(criteria.getOperation()).apply(criteria);
    }

    private Specification<Product> searchEquals(SearchCriteria<LocalDate> criteria) {
        return ProductSpecifications.hasProductCreateDateEquals(criteria.getValue());
    }

    private Specification<Product> searchMoreOrEquals(SearchCriteria<LocalDate> criteria) {
        return ProductSpecifications.hasProductCreateDateAfter(criteria.getValue());
    }

    private Specification<Product> searchLessOrEquals(SearchCriteria<LocalDate> criteria) {
        return ProductSpecifications.hasProductCreateDateBefore(criteria.getValue());
    }

    private Specification<Product> searchLike(SearchCriteria<LocalDate> criteria) {
        Specification<Product> specification = ProductSpecifications
                .hasProductCreateDateBefore(criteria.getValue().plusDays(DELTA_DAYS_FOR_LIKE));
        return specification.and(ProductSpecifications
                .hasProductCreateDateAfter(criteria.getValue().minusDays(DELTA_DAYS_FOR_LIKE)));
    }
}
