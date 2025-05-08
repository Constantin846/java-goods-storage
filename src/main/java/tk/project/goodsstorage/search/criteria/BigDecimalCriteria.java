package tk.project.goodsstorage.search.criteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.search.enums.Operation;
import tk.project.goodsstorage.search.strategy.BigDecimalPredicateStrategy;
import tk.project.goodsstorage.search.strategy.PredicateStrategy;

import java.math.BigDecimal;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class BigDecimalCriteria implements SearchCriteria<BigDecimal> {

    private static final PredicateStrategy<BigDecimal> strategy = new BigDecimalPredicateStrategy();

    @NotBlank(message = "Field of search criteria must be set")
    private final String field;

    @NotNull(message = "Value of search criteria must be set")
    private final BigDecimal value;

    @NotNull(message = "Operation of search criteria must be set")
    private final Operation operation;

    @Override
    public PredicateStrategy<BigDecimal> getStrategy() {
        return strategy;
    }
}
