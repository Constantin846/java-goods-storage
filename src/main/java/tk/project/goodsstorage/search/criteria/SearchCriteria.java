package tk.project.goodsstorage.search.criteria;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tk.project.goodsstorage.search.enums.Operation;
import tk.project.goodsstorage.search.strategy.PredicateStrategy;

@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME, include = JsonTypeInfo.As.PROPERTY,
        property = "field", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(names = {"name", "article", "description", "category"}, value = StringCriteria.class),
        @JsonSubTypes.Type(name = "price", value = BigDecimalCriteria.class),
        @JsonSubTypes.Type(name = "count", value = LongCriteria.class),
        @JsonSubTypes.Type(name = "lastCountUpdateTime", value = InstantCriteria.class),
        @JsonSubTypes.Type(name = "createDate", value = LocalDateCriteria.class)
})
public interface SearchCriteria<T> {

    PredicateStrategy<T> getStrategy();

    String getField();

    T getValue();

    Operation getOperation();
}
