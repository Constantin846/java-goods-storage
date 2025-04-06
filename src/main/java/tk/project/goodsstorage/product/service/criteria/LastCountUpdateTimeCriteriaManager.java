package tk.project.goodsstorage.product.service.criteria;

import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductSpecifications;

import java.time.Instant;
import java.util.Map;
import java.util.function.Function;

public class LastCountUpdateTimeCriteriaManager implements ProductFieldCriteriaManager<Instant> {
    private static final Long DELTA_SECONDS_FOR_LIKE = 2 * 24 * 60 * 60L;
    private static final Long DELTA_SECONDS_FOR_EQUALS = 5 * 60 * 60L;
    private static final Map<Operation, Function<SearchCriteria<Instant>, Specification<Product>>> FUNCTIONS = Map.of(
            Operation.EQUALS, LastCountUpdateTimeCriteriaManager::searchEquals,
            Operation.MORE_OR_EQUALS, LastCountUpdateTimeCriteriaManager::searchMoreOrEquals,
            Operation.LESS_OR_EQUALS, LastCountUpdateTimeCriteriaManager::searchLessOrEquals,
            Operation.LIKE, LastCountUpdateTimeCriteriaManager::searchLike
    );

    @Override
    public Specification<Product> getSpecification(SearchCriteria<Instant> criteria) {
        return FUNCTIONS.get(criteria.getOperation()).apply(criteria);
    }

    private static Specification<Product> searchEquals(SearchCriteria<Instant> criteria) {
        Specification<Product> specification = ProductSpecifications
                .hasProductLastCountUpdateTimeBefore(criteria.getValue().plusSeconds(DELTA_SECONDS_FOR_EQUALS));
        return specification.and(ProductSpecifications
                .hasProductLastCountUpdateTimeAfter(criteria.getValue().minusSeconds(DELTA_SECONDS_FOR_EQUALS)));
    }

    private static Specification<Product> searchMoreOrEquals(SearchCriteria<Instant> criteria) {
        return ProductSpecifications.hasProductLastCountUpdateTimeAfter(criteria.getValue());
    }

    private static Specification<Product> searchLessOrEquals(SearchCriteria<Instant> criteria) {
        return ProductSpecifications.hasProductLastCountUpdateTimeBefore(criteria.getValue());
    }

    private static Specification<Product> searchLike(SearchCriteria<Instant> criteria) {
        Specification<Product> specification = ProductSpecifications
                .hasProductLastCountUpdateTimeBefore(criteria.getValue().plusSeconds(DELTA_SECONDS_FOR_LIKE));
        return specification.and(ProductSpecifications
                .hasProductLastCountUpdateTimeAfter(criteria.getValue().minusSeconds(DELTA_SECONDS_FOR_LIKE)));
    }
}
