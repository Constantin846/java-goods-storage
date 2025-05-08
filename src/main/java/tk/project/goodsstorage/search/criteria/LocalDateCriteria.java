package tk.project.goodsstorage.search.criteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.search.enums.Operation;
import tk.project.goodsstorage.search.strategy.LocalDatePredicateStrategy;
import tk.project.goodsstorage.search.strategy.PredicateStrategy;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class LocalDateCriteria implements SearchCriteria<LocalDate> {

    private static final PredicateStrategy<LocalDate> strategy = new LocalDatePredicateStrategy();

    @NotBlank(message = "Field of search criteria must be set")
    private final String field;

    @NotNull(message = "Value of search criteria must be set")
    private final LocalDate value;

    @NotNull(message = "Operation of search criteria must be set")
    private final Operation operation;

    @Override
    public PredicateStrategy<LocalDate> getStrategy() {
        return strategy;
    }
}
