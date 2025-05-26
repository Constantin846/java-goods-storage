package tk.project.goodsstorage.search.criteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.search.enums.Operation;
import tk.project.goodsstorage.search.strategy.PredicateStrategy;
import tk.project.goodsstorage.search.strategy.StringPredicateStrategy;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class StringCriteria implements SearchCriteria<String> {

    private static final PredicateStrategy<String> STRATEGY = new StringPredicateStrategy();

    @NotBlank(message = "Field of search criteria must be set")
    private final String field;

    @NotBlank(message = "Value of search criteria must be set")
    private final String value;

    @NotNull(message = "Operation of search criteria must be set")
    private final Operation operation;

    @Override
    public PredicateStrategy<String> getStrategy() {
        return STRATEGY;
    }
}
