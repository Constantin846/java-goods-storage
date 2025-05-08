package tk.project.goodsstorage.search.criteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.search.enums.Operation;
import tk.project.goodsstorage.search.strategy.LongPredicateStrategy;
import tk.project.goodsstorage.search.strategy.PredicateStrategy;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class LongCriteria implements SearchCriteria<Long> {

    private static final PredicateStrategy<Long> strategy = new LongPredicateStrategy();

    @NotBlank(message = "Field of search criteria must be set")
    private final String field;

    @NotNull(message = "Value of search criteria must be set")
    private final Long value;

    @NotNull(message = "Operation of search criteria must be set")
    private final Operation operation;

    @Override
    public PredicateStrategy<Long> getStrategy() {
        return strategy;
    }
}
