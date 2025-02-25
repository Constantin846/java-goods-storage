package tk.project.goodsstorage.product.dto.find.criteria;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME, include = JsonTypeInfo.As.PROPERTY,
        property = "field", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(names = {"name", "article", "description", "category"}, value = StringCriteria.class),
        @JsonSubTypes.Type(name = "price", value = BigDecimalCriteria.class),
        @JsonSubTypes.Type(name = "count", value = LongCriteria.class),
        @JsonSubTypes.Type(name = "lastCountUpdateTime", value = InstantCriteria.class),
        @JsonSubTypes.Type(name = "createDate", value = LocalDateCriteria.class)
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SearchCriteria<T> {

    @NotBlank(message = "Field of search criteria must be set")
    String field;

    T value;

    @NotNull(message = "Operation of search criteria must be set")
    Operation operation;
}
