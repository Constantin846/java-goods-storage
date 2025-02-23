package tk.project.goodsstorage.product.service.criteria;

import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.product.dto.find.criteria.Operation;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductSpecifications;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LastCountUpdateTimeCriteriaManager implements ItemCriteriaManager<Instant> {
    private static final Long DELTA_SECONDS_FOR_LIKE = 2 * 24 * 60 * 60L;
    private static final Long DELTA_SECONDS_FOR_EQUALS = 5 * 60 * 60L;
    private final Map<Operation, Function<SearchCriteria<Instant>, Specification<Product>>> functions;

    public LastCountUpdateTimeCriteriaManager() {
        functions = new HashMap<>();
        functions.put(Operation.EQUALS, this::searchEquals);
        functions.put(Operation.MORE_OR_EQUALS, this::searchMoreOrEquals);
        functions.put(Operation.LESS_OR_EQUALS, this::searchLessOrEquals);
        functions.put(Operation.LIKE, this::searchLike);
    }

    @Override
    public Specification<Product> getSpecification(SearchCriteria<Instant> criteria) {
        return functions.get(criteria.getOperation()).apply(criteria);
    }

    private Specification<Product> searchEquals(SearchCriteria<Instant> criteria) {
        Specification<Product> specification = ProductSpecifications
                .hasProductLastCountUpdateTimeBefore(criteria.getValue().plusSeconds(DELTA_SECONDS_FOR_EQUALS));
        return specification.and(ProductSpecifications
                .hasProductLastCountUpdateTimeAfter(criteria.getValue().minusSeconds(DELTA_SECONDS_FOR_EQUALS)));
    }

    private Specification<Product> searchMoreOrEquals(SearchCriteria<Instant> criteria) {
        return ProductSpecifications.hasProductLastCountUpdateTimeAfter(criteria.getValue());
    }

    private Specification<Product> searchLessOrEquals(SearchCriteria<Instant> criteria) {
        return ProductSpecifications.hasProductLastCountUpdateTimeBefore(criteria.getValue());
    }

    private Specification<Product> searchLike(SearchCriteria<Instant> criteria) {
        Specification<Product> specification = ProductSpecifications
                .hasProductLastCountUpdateTimeBefore(criteria.getValue().plusSeconds(DELTA_SECONDS_FOR_LIKE));
        return specification.and(ProductSpecifications
                .hasProductLastCountUpdateTimeAfter(criteria.getValue().minusSeconds(DELTA_SECONDS_FOR_LIKE)));
    }
}
