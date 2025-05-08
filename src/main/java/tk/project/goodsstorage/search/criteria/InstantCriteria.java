package tk.project.goodsstorage.search.criteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.search.enums.Operation;
import tk.project.goodsstorage.search.strategy.InstantPredicateStrategy;
import tk.project.goodsstorage.search.strategy.PredicateStrategy;

import java.time.Instant;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class InstantCriteria implements SearchCriteria<Instant> {

    private static final PredicateStrategy<Instant> strategy = new InstantPredicateStrategy();

    @NotBlank(message = "Field of search criteria must be set")
    private final String field;

    @NotNull(message = "Value of search criteria must be set") // format "2025-02-22T14:32:00Z"
    private final Instant value;

    @NotNull(message = "Operation of search criteria must be set")
    private final Operation operation;

    @Override
    public PredicateStrategy<Instant> getStrategy() {
        return strategy;
    }
}
